package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.classification.FGItems;
import me.camm.productions.fortressguns.item.classification.ingredients.FGSimpleIngredient;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;
import java.util.HashSet;

public class IBSimpleIngredient implements InteractionBehaviourItem {

    @Override
    public Material[] getLabels() {
        Material[] materials = new Material[FGItems.SIMPLE_INGREDIENTS.length];
        for (int i = 0; i < materials.length; i++) {
            materials[i] = FGItems.SIMPLE_INGREDIENTS[i].get().getType();
        }
        materials = new HashSet<>(Arrays.asList(materials)).toArray(new Material[0]);

        return materials;
    }

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        if (item == null || item.getB() == null || item.getB().getType() == Material.AIR) {
            return false;
        }

        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item.getB())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) { // if there is no result, no need to do stuff
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
    }

    @Override
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) { // if there is no result, no need to do stuff
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
    }
}
