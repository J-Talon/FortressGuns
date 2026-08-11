package me.camm.productions.fortressguns.item.interact;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public interface InteractionBehaviourItem extends InteractionBehaviour<ItemStack> {


    public Material[] getLabels();

    public default void onRCAir(PlayerInteractEvent event) {}

    public default void onLCAir(PlayerInteractEvent event) {}


    public default void onRCBlock(PlayerInteractEvent event) {}

    public default void onBlockPlace(BlockPlaceEvent event) {}

    public default void onLCBlock(PlayerInteractEvent event) {}

    //may need to change in the future as interact @ entity event or ASManipulate are more appropriate
    public default void onRCEntity(PlayerInteractEntityEvent event) {}

    public default void onScroll(PlayerItemHeldEvent event) {}

}
