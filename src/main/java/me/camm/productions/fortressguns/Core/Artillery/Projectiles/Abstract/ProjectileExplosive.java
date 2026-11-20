package me.camm.productions.fortressguns.Core.Artillery.Projectiles.Abstract;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public interface ProjectileExplosive {

    void explode(@Nullable Vector hit);

    float getExplosionPower();

}
