package me.camm.productions.fortressguns.Artillery.Entities.Property;

public interface IAutoTracking extends IAimed {
    boolean isAiming();

    void setAiming(boolean aiming);

    void startAiming();
}
