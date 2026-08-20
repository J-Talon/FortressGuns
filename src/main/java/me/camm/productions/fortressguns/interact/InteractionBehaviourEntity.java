package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public interface InteractionBehaviourEntity extends InteractionBehaviour<EntityType> {

    public void onRCEntity(PlayerInteractEntityEvent event);
    public void onLCEntity(Player player, ItemStack mainhand, Entity hit, EntityDamageByEntityEvent event);


}
