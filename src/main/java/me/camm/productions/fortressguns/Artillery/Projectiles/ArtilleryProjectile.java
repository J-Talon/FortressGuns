package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import javax.annotation.Nullable;

public interface ArtilleryProjectile {

     public abstract void preHit(@Nullable MovingObjectPosition hitPosition);

     @FunctionalInterface
     interface SoundPlayer {
          void playSound(Location loc);
     }



     public abstract float getDamageStrength();

     default Vec3D stepBack(MovingObjectPosition pos, Entity e) {
          final Vec3D vec3d = pos.getPos().a(e.locX(), e.locY(), e.locZ());
          final Vec3D vec3d2 = vec3d.d().a(0.05000000074505806);  //DON'T ASK
          return new Vec3D(e.locX() - vec3d2.b, e.locY() - vec3d2.c, e.locZ() - vec3d2.d);
     }



}
