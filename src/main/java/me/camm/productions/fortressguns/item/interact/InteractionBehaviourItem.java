package me.camm.productions.fortressguns.item.interact;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public interface InteractionBehaviourItem extends InteractionBehaviour<Tuple2<Player, ItemStack>> {


    public Material[] getLabels();

    public default void onRCAir(PlayerInteractEvent event) {}

    public default void onLCAir(PlayerInteractEvent event) {}


    public default void onRCBlock(PlayerInteractEvent event) {}

    public default void onBlockPlace(BlockPlaceEvent event) {}

    public default void onLCBlock(PlayerInteractEvent event) {}

    //may need to change in the future as interact @ entity event or ASManipulate are more appropriate
    public default void onRCEntity(PlayerInteractEntityEvent event) {}

    public default void onScroll(PlayerItemHeldEvent event) {}

    public default void onDispense(BlockDispenseEvent event) {}

    public default void onItemConsume(PlayerItemConsumeEvent event) {}

    public default void onBowShoot(EntityShootBowEvent event) {}

    public default void onCraft(CraftItemEvent event) {}

    public default void onPrepareCraft(PrepareItemCraftEvent event) {}


    //left clicks for entities (attacking) are handled in entity damage event which I will tackle later
}
