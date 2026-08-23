package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.ArtilleryRideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.item.ItemUtils;
import me.camm.productions.fortressguns.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import net.minecraft.world.entity.Entity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Set;

public class IBAmmoItem implements InteractionBehaviourItem {

    private static Material[] labels = null;

    @Override
    public boolean accept(Tuple2<Player, ItemStack> tup) {
        ItemStack stack = tup.getB();
        return ItemUtils.isAmmoItem(stack) != null;
    }


    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @Override
    public Material[] getLabels() {

        if (labels != null)
            return labels;
        Set<Material> mats = new HashSet<>();
        for (AmmoItem item: AmmoItem.values()) {
            mats.add(item.getMat());
        }

        labels = new Material[mats.size()];
        int i = 0;
        for (Material mat: mats) {
            labels[i] = mat;
            i ++;
        }

        return labels;
    }

    @Override
    public void onRCEntity(PlayerInteractEntityEvent event) {
        this.handleInteraction(event.getPlayer());
    }


    @Override
    public void onRCAir(PlayerInteractEvent event) {
        this.handleInteraction(event.getPlayer());
    }





    private void handleInteraction(Player player) {

            org.bukkit.entity.Entity ride = player.getVehicle();
            if (ride == null || !ride.isValid() || ride.isDead())
                return;

            Entity nms = ((CraftEntity)ride).getHandle();


            if (!(nms instanceof ComponentAS)) {
                return;
            }

            Construct cons = ((ComponentAS) nms).getBody();

            if (!(cons instanceof ArtilleryRideable rideable)) {
                return;
            }

            InventoryGroup group = rideable.getInventoryGroup();
            if (rideable instanceof RapidFire rapid && rapid.isJammed()) {
                group.openInventory(InventoryCategory.JAM_CLEAR, player);
            }
            else {
                group.openInventory(InventoryCategory.RELOADING, player);
            }
    }

    @Override
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            // this custom item is legitimately required by the recipe.
            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (accept(new Tuple2<>(null, item))) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @Override
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            // this custom item is legitimately required by the recipe.
            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (accept(new Tuple2<>(event.getWhoClicked() instanceof Player player ? player : null, item))) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
