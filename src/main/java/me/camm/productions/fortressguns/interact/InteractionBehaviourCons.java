package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public interface InteractionBehaviourCons extends InteractionBehaviour<Construct> {

    /*
    handleInteraction(itemstack, player)
    artillerypart
    handleInteraction()
    --> part of these could both be inlined into item behaviour

    but part of it also seems to be for air when interacting with entity?

    artillery part

    onRCEntity -->
    onLCEntity -->
    onRCPrecise -->

    rules:
    lc is damage event
     */

    public void onRCCons(Construct struct, Component component, PlayerInteractEntityEvent event);

    //EntityDamageByEntityEvent
    public void onLCCons(Construct struct, Component component, Player player, ItemStack stack, EntityDamageByEntityEvent event);

}
