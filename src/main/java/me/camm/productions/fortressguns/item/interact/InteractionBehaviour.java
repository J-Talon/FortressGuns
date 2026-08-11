package me.camm.productions.fortressguns.item.interact;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface InteractionBehaviour<T> {


    public abstract boolean accept(T item);

    public @Nullable default IBHandle getHandle() {return null;}


}
