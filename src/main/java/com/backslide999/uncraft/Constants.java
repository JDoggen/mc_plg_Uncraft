package com.backslide999.uncraft;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

    public static List<Material> disabledMaterials;

    public static HashMap<Material, List<ItemStack>> defaultRecipes;

    public static HashMap<Material, Integer> defaultRecipesAmount;

    public static void reload(){
        Constants.reload(null);
    }

    public static void reload(CommandSender sender){
        List<Material> disabledMaterials = new ArrayList<>();
        HashMap<Material, List<ItemStack>> defaultRecipes = new HashMap<>();
        HashMap<Material, Integer> defaultRecipesAmount= new HashMap();

        List<String> rawDisabledMaterials = UncraftPlugin.getInstance().fetchConfigStringList("recipes.disabled");
        rawDisabledMaterials.forEach(x -> {
            try{
                disabledMaterials.add(Material.valueOf(x));
            } catch(IllegalArgumentException e) {
                UncraftPlugin.getInstance().logWarning("Invalid material found in disabled list: [" + x + "]");
                if(sender != null){
                    UncraftPlugin.getInstance().sendPlayerWarning(sender,
                            "Invalid material found in disabled list: [" + x + "]");
                }
            }
        });

        List<String> rawDefaultRecipes = UncraftPlugin.getInstance().fetchConfigStringList("recipes.default.materials");
        rawDefaultRecipes.forEach(recipeResult -> {
            Material defaultMaterial = null;
            List<ItemStack> recipe = new ArrayList<>();
            boolean error = false;
            try{
                defaultMaterial = Material.valueOf(recipeResult);
            } catch(IllegalArgumentException e){
                error = true;
                UncraftPlugin.getInstance().logWarning("Invalid material found for default recipe: [" + recipeResult + "]");
                if(sender != null){
                    UncraftPlugin.getInstance().sendPlayerWarning(sender, "Invalid material found for default recipe result: [" + recipeResult + "]");
                }
            }

            List<String> defaultRecipe = UncraftPlugin.getInstance().fetchConfigStringList("recipes.default.recipes." + recipeResult + ".recipe");
            for(String material: defaultRecipe){
                try{
                    recipe.add(new ItemStack(Material.valueOf(material), 1));
                } catch(IllegalArgumentException e){
                    error = true;
                    UncraftPlugin.getInstance().logWarning("Invalid material found in default recipe: [" + material + "] for recipe [" + recipeResult +  "]");
                    if(sender != null){
                        UncraftPlugin.getInstance().sendPlayerWarning(sender,
                                "Invalid material found in default recipe: [" + material + "] for recipe [" + recipeResult +  "]");
                    }
                }
            }

            Integer amount = null;
            try{
                amount = UncraftPlugin.getInstance().fetchConfigInteger("recipes.default.recipes." + recipeResult + ".amount");
            } catch(Exception e){
                error = true;
                UncraftPlugin.getInstance().logWarning("Invalid amount found in for recipe: [" + defaultMaterial+ "]");
                if(sender != null){
                    UncraftPlugin.getInstance().sendPlayerWarning(sender,
                            "Invalid amount found in for recipe: [" + defaultMaterial+ "]");
                }
            }

            if(!error){
                defaultRecipes.put(defaultMaterial, recipe);
                defaultRecipesAmount.put(defaultMaterial, amount);
            }
        });



        Constants.disabledMaterials  = disabledMaterials;
        Constants.defaultRecipes = defaultRecipes;
        Constants.defaultRecipesAmount = defaultRecipesAmount;
    }

    public static boolean isDisabled(Material material){
        return Constants.disabledMaterials.contains(material);
    }

    public static List<ItemStack> getDefaultRecipe(Material material){
        return Constants.defaultRecipes.get(material);
    }

    public static Integer getDefaultRecipeAmount(Material material) {
        return Constants.defaultRecipesAmount.get(material);
    }




}
