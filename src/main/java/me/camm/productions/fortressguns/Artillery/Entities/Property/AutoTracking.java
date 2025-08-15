package me.camm.productions.fortressguns.Artillery.Entities.Property;

import net.minecraft.world.entity.Entity;

public interface AutoTracking {
    boolean isAiming();
    boolean setTarget(Entity target);

    void setAiming(boolean aiming);

    void startAiming();
}
