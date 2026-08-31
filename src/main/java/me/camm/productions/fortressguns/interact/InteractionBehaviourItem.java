package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
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

    public default void onScroll(PlayerItemHeldEvent event) {}

    public default void onDispense(BlockDispenseEvent event) {}

    public default void onItemConsume(PlayerItemConsumeEvent event) {}

    public default void onBowShoot(EntityShootBowEvent event) {}

    public default void onCraft(CraftItemEvent event) {}

    public default void onPrepareCraft(PrepareItemCraftEvent event) {}

    public default void onFurnaceBurn(FurnaceBurnEvent event) {}

    public default void onFurnaceSmelt(FurnaceSmeltEvent event) {}

    public default void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {}

    public default void onFurnaceExtract(FurnaceExtractEvent event) {}

    public default void onBrew(BrewEvent event) {}

    public default void onBrewingStandFuel(BrewingStandFuelEvent event) {}

    public default void onEnchantItem(EnchantItemEvent event) {}

    public default void onPrepareEnchant(PrepareItemEnchantEvent event) {}

    public default void onPrepareAnvil(PrepareAnvilEvent event) {}

    public default void onPrepareSmithing(PrepareSmithingEvent event) {}

    public default void onSmith(SmithItemEvent event) {}


    //left clicks for entities (attacking) are handled in entity damage event which I will tackle later
}
