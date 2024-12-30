package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Util.Explosions.ShellExplosion;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public interface ProjectileExplosive {

    public void explode(@Nullable Vec3D hit);

    default void modifyExplosion(ShellExplosion explosion) {

    }

    abstract float getExplosionPower();

}
