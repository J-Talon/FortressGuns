package me.camm.productions.fortressguns.item.interact;

import me.camm.productions.fortressguns.item.ArtilleryItems.ItemUtils;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class IBConstructBox implements ItemBehaviour {

    @Override
    public boolean accept(ItemStack stack) {
        return ItemUtils.holdsConstruct(stack) != null;
    }


    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }


    @Override
    public void onRCAir(PlayerInteractEvent event) {




    }
}
