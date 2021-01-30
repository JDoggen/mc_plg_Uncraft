package com.backslide999.uncraft.commands;

import com.backslide999.uncraft.Constants;
import com.backslide999.uncraft.UncraftPlugin;
import com.backslide999.uncraft.containers.Mode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.MessageFormat;
import java.util.*;

public class Uncraft {

    public Uncraft(Player player, Mode mode){
        if(!player.hasPermission("uncraft.uncraft")){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "unauthorized");
            return;
        }

        // Check what item is held by player
        int selected = player.getInventory().getHeldItemSlot();
        ItemStack itemStack = player.getInventory().getItem(selected);

        if(itemStack == null){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "hold_item_first");
            return;
        }
        Map<Enchantment, Integer> enchants = itemStack.getEnchantments();
        ItemMeta itemMeta = itemStack.getItemMeta();
        // Check if item is valid
        if(Constants.isDisabled(itemStack.getType())){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "currently_disabled");
            return;
        }

        // Check recipe for held item
        Material material = itemStack.getType();
        int amount = itemStack.getAmount();
        Recipe recipe = Bukkit.getRecipesFor(itemStack)
                .stream()
                .filter(x -> x instanceof ShapedRecipe || x instanceof ShapelessRecipe)
                .findFirst()
                .orElse(null);

        // Check if default recipe
        List<ItemStack> defaultRecipe = Constants.getDefaultRecipe(material);
        Integer defaultRecipeAmount = Constants.getDefaultRecipeAmount(material);
        boolean useDefault = defaultRecipe != null;


        if(recipe == null && !useDefault){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "no_recipe_found");
            return;
        }

        boolean continueUncrafting = mode == Mode.ALL;
        int loopcount = 0;
        do {
            loopcount++;
            // Take crafting resources from player
            ItemStack craftable = null;
            int toRemoveAmount = 0;
            if (useDefault) {
                craftable = new ItemStack(material, defaultRecipeAmount);
                toRemoveAmount = defaultRecipeAmount;
            } else {
                craftable = recipe.getResult();
                toRemoveAmount = craftable.getAmount();
            }
            // If enchants available on item, add them to the item to remove
            craftable.addEnchantments(enchants);
            craftable.setItemMeta(itemMeta);

            // Check if enough items removed to succesfully craft item. If not -> give back
            HashMap<Integer, ItemStack> notRemoved = player.getInventory().removeItem(craftable);
            if (!notRemoved.isEmpty()) {
                continueUncrafting = false;
                int removed = toRemoveAmount - notRemoved.get(0).getAmount();
                ItemStack refund = new ItemStack(craftable.getType(), removed);
                player.getInventory().addItem(refund);
                Integer[] format = {toRemoveAmount};
                if(loopcount == 1){
                    UncraftPlugin.getInstance().sendPlayerWarning(
                            player,
                            new MessageFormat(
                                    UncraftPlugin.getInstance().fetchConfigString("messages.warning.not_enough_items")
                            ).format(format)
                    );
                }
                break;
            }


            Collection<ItemStack> materials = null;
            if (useDefault) {
                materials = defaultRecipe;
            } else if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe) recipe;
                materials = shaped.getIngredientMap().values();
            } else if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                materials = shapeless.getIngredientList();
            }

            materials.removeIf(Objects::isNull);

            if (materials == null) {
                UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "no_recipe_found");
                return;
            }
            materials
                    .stream()
                    .map(x -> player.getInventory().addItem(x))
                    .forEach(rest -> {
                        if (rest == null || rest.isEmpty())
                            return;
                        ItemStack restItems = rest.get(0);
                        if (restItems != null) {
                            player.getLocation().getWorld().dropItem(player.getLocation(), restItems);
                        }
                    });

            // Failsafe:
            if(loopcount == UncraftPlugin.getInstance().fetchConfigInteger("config.max_loop_count")){
                UncraftPlugin.getInstance().logWarning("##############################################################");
                UncraftPlugin.getInstance().logWarning("#Max loop counter reached. This should never happen. Please   ");
                UncraftPlugin.getInstance().logWarning("#provide following details when issueing a bug report:        ");
                UncraftPlugin.getInstance().logWarning("#Mat:  " + material + " UseDef: " + useDefault);
                UncraftPlugin.getInstance().logWarning("#ToRA: " + toRemoveAmount + "RecLen: " + materials.size() );
                UncraftPlugin.getInstance().logWarning("*#############################################################");
                break;
            }
        } while(continueUncrafting);
    }
}
