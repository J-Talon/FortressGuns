package me.camm.productions.fortressguns.Recipes;

import me.camm.productions.fortressguns.item.classification.FGItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

public final class RecipeManager {

    private static final Map<NamespacedKey, Recipe> FG_RECIPES = new HashMap<>();

    private RecipeManager() {
    }

    public static void registerRecipes() {
        ShapedRecipe flareRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "flare_recipe"),
                        FGItems.FLARE.get()
                )
                .shape(
                        " R ",
                        "RFR",
                        " R "
                )
                .ingredient('R', Material.REDSTONE)
                .ingredient('F', Material.FIREWORK_ROCKET)
                .register();

        ShapedRecipe flareGunRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "flare_gun_recipe"),
                        FGItems.FLARE_GUN.get()
                )
                .shape(
                        "III",
                        "I I",
                        " R "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe CRAMRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "CRAM_recipe"),
                        FGItems.CRAM.get()
                )
                .shape(
                        "NNN",
                        "NBN",
                        " R "
                )
                .ingredient('I', Material.NETHERITE_INGOT)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.BOW)
                .register();
    }

    public static void register(NamespacedKey key, Recipe recipe) {
        FG_RECIPES.put(key, recipe);
        Bukkit.addRecipe(recipe);
    }

    public static boolean isCustomRecipe(Recipe recipe) {

        if (recipe instanceof ShapedRecipe shaped) {
            return FG_RECIPES.containsKey(shaped.getKey());
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            return FG_RECIPES.containsKey(shapeless.getKey());
        }

        return false;
    }
}