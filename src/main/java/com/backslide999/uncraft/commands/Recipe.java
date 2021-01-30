package com.backslide999.uncraft.commands;

import com.backslide999.uncraft.Constants;
import com.backslide999.uncraft.UncraftPlugin;
import com.backslide999.uncraft.containers.Mode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.text.MessageFormat;
import java.util.*;

public class Recipe {

    public Recipe(Player player){
        if(!player.hasPermission("uncraft.recipe")){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player,"unauthorized");
            return;
        }
        // Check what item is held by player
        int selected = player.getInventory().getHeldItemSlot();
        ItemStack itemStack = player.getInventory().getItem(selected);

        if(itemStack == null){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "hold_item_first");
            return;
        }

        // Check if item is valid
        if(Constants.isDisabled(itemStack.getType())){
            UncraftPlugin.getInstance().sendPlayerDefaultWarning(player, "currently_disabled");
            return;
        }

        // Check recipe for held item
        Material material = itemStack.getType();
        int amount = itemStack.getAmount();
        org.bukkit.inventory.Recipe recipe = Bukkit.getRecipesFor(itemStack)
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

        // Get the result of the recipe
        ItemStack craftable = null;
        int toRemoveAmount = 0;
        if (useDefault) {
            craftable = new ItemStack(material, defaultRecipeAmount);
            toRemoveAmount = defaultRecipeAmount;
        } else {
            craftable = recipe.getResult();
            toRemoveAmount = craftable.getAmount();
        }

        // Get the resources of the recipe
        Collection<ItemStack> resources = null;
        if (useDefault) {
            resources = defaultRecipe;
        } else if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) recipe;
            resources = shaped.getIngredientMap().values();
        } else if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
            resources = shapeless.getIngredientList();
        }
        resources.removeIf(Objects::isNull);


        List<String> messages = new ArrayList<>();
        messages.add(
                new MessageFormat(
                    UncraftPlugin.getInstance().fetchConfigString("messages.info.uncraft_recipe_result")
                ).format(new Object[]{craftable.getType(), toRemoveAmount})
        );

        HashMap<Material, Integer> resultSet = new HashMap<>();
        for(ItemStack resource: resources){
            Integer resourceAmount = resultSet.containsKey(resource.getType()) ? resultSet.get(resource.getType()) : 0;
            resourceAmount += resource.getAmount();
            resultSet.put(resource.getType(), resourceAmount);
        }

        for(Material resourceMaterial: resultSet.keySet()){
            messages.add(
                    new MessageFormat(
                            UncraftPlugin.getInstance().fetchConfigString("messages.info.uncraft_recipe_resource")
                    ).format(new Object[]{resourceMaterial, resultSet.get(resourceMaterial)})
            );
        }

        UncraftPlugin.getInstance().sendPlayerInfo(player, messages);
    }
}
