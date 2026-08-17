package me.camm.productions.fortressguns.Recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public final class RecipeBuilder {

    private final ShapedRecipe recipe;

    private RecipeBuilder(
            NamespacedKey key,
            ItemStack result
    ) {
        this.recipe = new ShapedRecipe(key, result);
    }

    public static RecipeBuilder create(
            NamespacedKey key,
            ItemStack result
    ) {
        return new RecipeBuilder(key, result);
    }

    public RecipeBuilder shape(String... shape) {
        recipe.shape(shape);
        return this;
    }

    public RecipeBuilder ingredient(
            char key,
            Material material
    ) {
        recipe.setIngredient(key, material);
        return this;
    }

    public ShapedRecipe register() {
        RecipeManager.register(recipe.getKey(), recipe);
        return recipe;
    }
}