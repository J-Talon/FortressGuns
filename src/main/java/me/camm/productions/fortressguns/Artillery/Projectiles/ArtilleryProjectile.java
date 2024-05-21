package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

public interface ArtilleryProjectile {

     abstract void explode(MovingObjectPosition hitPosition);

     @FunctionalInterface
     interface SoundPlayer {
          void playSound(Location loc);
     }


}
