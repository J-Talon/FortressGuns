package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.item.classification.ingredients.FGSimpleIngredient;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
    public void onItemConsume(PlayerItemConsumeEvent event) {
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

    @Override
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        ItemStack item = event.getFuel();

        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        ItemStack item = event.getSource();
        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                event.setTotalCookTime(0);
                return;
            }
        }
    }

    @Override
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack item = event.getSource();
        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                event.setCancelled(true);
                return;
            }
        }

        item = event.getResult();
        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onFurnaceExtract(FurnaceExtractEvent event) { // when output is taken out
        ItemStack item = (ItemStack) event.getBlock();
        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                // CANCEL
                return;
            }
        }
    }

    @Override
    public void onBrew(BrewEvent event) {
        for (ItemStack item : event.getContents().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onBrewingStandFuel(BrewingStandFuelEvent event) {
        ItemStack item = (ItemStack) event.getFuel();
        for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
            if (ingredient.isSimilar(item)) {
                event.setCancelled(true);
                return;
            }
        }

    }

    @Override
    public void onEnchantItem(EnchantItemEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setResult(null);
                    return;
                }
            }
        }
    }

    @Override
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setResult(null);
                    return;
                }
            }
        }
    }

    @Override
    public void onSmith(SmithItemEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            for (FGSimpleIngredient ingredient : FGItems.SIMPLE_INGREDIENTS) {
                if (ingredient.isSimilar(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
