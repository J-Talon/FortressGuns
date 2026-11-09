package me.camm.productions.fortressguns.Util.damage.builder;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface FGProjectileDamage {

    //depending on if we decide to make projectile multi-block, a projectile can be an arbitrary entity
    public Entity getProjectile();

    public @Nullable Entity getShooter();


}




