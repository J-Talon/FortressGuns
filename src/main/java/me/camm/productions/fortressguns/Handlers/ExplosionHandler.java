package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityExplodeEvent;


public class ExplosionHandler implements Listener
{

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        org.bukkit.entity.Entity entity = event.getEntity();
        Entity nms = ((CraftEntity)entity).getHandle();

    }
}
