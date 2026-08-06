package me.camm.productions.fortressguns.item.interact;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface ItemBehaviour {


    public abstract boolean accept(ItemStack stack);


    public default void onRCAir(PlayerInteractEvent event) {}

    public default void onLCAir(PlayerInteractEvent event) {}


    public default void onRCBlock(PlayerInteractEvent event) {}

    public default void onBlockPlace(BlockPlaceEvent event) {}

    public default void onLCBlock(PlayerInteractEvent event) {}


    //todo you may need to switch this depending on whether
    //as manipulate event or player interact @ entity event is more suitable
    public default void onRCEntity(PlayerInteractEntityEvent event) {}

    public default void onLCEntity(PlayerInteractEntityEvent event) {}




}
