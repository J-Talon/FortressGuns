package me.camm.productions.fortressguns.item.interact;

import me.camm.productions.fortressguns.item.ArtilleryItems.ItemUtils;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class IBAmmoItem implements ItemBehaviour {


    @Override
    public boolean accept(ItemStack stack) {
        return ItemUtils.isAmmoItem(stack) != null;
    }


    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
