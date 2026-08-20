package me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.ArtilleryRideable;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import net.minecraft.world.entity.Entity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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


            if (!(nms instanceof Component)) {
                return;
            }

            Construct cons = ((Component) nms).getBody();

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
}
