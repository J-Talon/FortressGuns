package me.camm.productions.fortressguns.item.interact;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface InteractionBehaviour<T> {


    public abstract boolean accept(T item);

    //if accept() returns true, should it activate functions of other items which fit the same description?
    public default boolean chain() {return false;}

    public @Nullable default IBHandle getHandle() {return null;}


}
