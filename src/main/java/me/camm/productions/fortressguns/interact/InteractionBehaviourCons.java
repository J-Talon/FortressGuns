package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.Math.Tuple3;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public interface InteractionBehaviourCons extends InteractionBehaviour<Tuple3<ConstructType, ItemStack, Player>> {

    /*
    tuple3 <cons, mainhand, player>
    onRCEntity -->
    onLCEntity -->
     */

    public void onRCCons(Construct struct, Component component, ItemStack mainHand, PlayerInteractEntityEvent event);

    //EntityDamageByEntityEvent
    public void onLCCons(Construct struct, Component component, Player player, ItemStack mainHand, EntityDamageByEntityEvent event);

}
