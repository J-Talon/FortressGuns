package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
[heatSeekerMissile]
explosionPower = 4
explosionDamage = 30
orbitDist = 17
explosionProximity = 3.16
fuelTicks = 600
maxSpeed = 2.44
acceleration = 0.2
maxAcceleration = 0.3

 */
public class SectionHeatMissile extends ConfigSection {

    private double explosionPower;
    private double explosionDamage;
    private double orbitDist;
    private double proximity;
    private double fuelTicks;
    private double maxSpeed;
    private double acceleration;
    private double maxAcceleration;

    public SectionHeatMissile(double explosionPower, double explosionDamage, double orbitDist, double proximity, double fuelTicks, double maxSpeed, double acceleration, double maxAcceleration) {
        this.explosionPower = explosionPower;
        this.explosionDamage = explosionDamage;
        this.orbitDist = orbitDist;
        this.proximity = proximity;
        this.fuelTicks = fuelTicks;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    public double getExplosionDamage() {
        return explosionDamage;
    }

    public double getOrbitDist() {
        return orbitDist;
    }

    public double getProximity() {
        return proximity;
    }

    public double getFuelTicks() {
        return fuelTicks;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }
}
