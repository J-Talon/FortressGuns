package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated  //armorstand interactions are handled different differently than regular entities,
             //meaning that behaviour cannot be handled purely with behaviours
public interface InteractionBehaviourCons extends InteractionBehaviour<Tuple2<Player, ItemStack>> {

    //setting this to null will cause it to be a wildcard; any item/type will match the labels
    //and any defined labels below will be ignored
    public default boolean treatGlobally() {
        return false;
    }

    public @NotNull ConstructType[] getPrimaryLabels();

    public @NotNull Material[] getSecondaryLabels();

    public default void onRCCons(Construct struct, Component component, ItemStack mainHand, PlayerInteractEntityEvent event) {};

    //EntityDamageByEntityEvent
    public default void onLCCons(Construct struct, Component component, Player player, ItemStack mainHand, EntityDamageByEntityEvent event) {};

}
