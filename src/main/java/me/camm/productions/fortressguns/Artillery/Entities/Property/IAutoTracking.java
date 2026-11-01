package me.camm.productions.fortressguns.Artillery.Entities.Property;

public interface IAutoTracking extends ITracking {
    boolean isAiming();

    void setAiming(boolean aiming);

    void startAiming();
}
