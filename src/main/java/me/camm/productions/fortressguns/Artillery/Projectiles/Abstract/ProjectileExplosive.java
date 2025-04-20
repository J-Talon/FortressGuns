package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public interface ProjectileExplosive {

    public void explode(@Nullable Vec3D hit);

    abstract float getExplosionPower();

}
