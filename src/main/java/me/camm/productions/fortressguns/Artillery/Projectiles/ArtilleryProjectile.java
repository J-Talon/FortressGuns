package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.entity.EntityExplodeEvent;

public interface ArtilleryProjectile {

     abstract void explode(MovingObjectPosition hitPosition);

     @FunctionalInterface
     interface SoundPlayer {
          void playSound(Location loc);
     }

     public default void postExplosion(EntityExplodeEvent event) {
          World world = event.getEntity().getWorld();

          boolean rule = Boolean.TRUE.equals(world.getGameRuleValue(GameRule.MOB_GRIEFING));
          if (!rule) {
               event.blockList().clear();
          }
     }


     public default void playExplosionEffects(Location explosion) {

     }


     public abstract float getStrength();

     default Vec3D getHitLoc(MovingObjectPosition pos, Entity e) {
          final Vec3D vec3d = pos.getPos().a(e.locX(), e.locY(), e.locZ());
          final Vec3D vec3d2 = vec3d.d().a(0.05000000074505806);  //DON'T ASK
          return new Vec3D(e.locX() - vec3d2.b, e.locY() - vec3d2.c, e.locZ() - vec3d2.d);
     }



}
