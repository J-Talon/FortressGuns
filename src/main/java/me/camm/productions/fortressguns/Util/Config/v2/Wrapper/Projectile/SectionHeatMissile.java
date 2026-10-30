package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

import java.util.logging.Logger;

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

    private Double explosionPower;
    private Double explosionDamage;
    private Double orbitDist;
    private Double proximity;
    private Long fuelTicks;
    private Double maxSpeed;
    private Double acceleration;
    private Double maxAcceleration;

    public SectionHeatMissile(Double explosionPower, Double explosionDamage, Double orbitDist, Double proximity, Long fuelTicks, Double maxSpeed, Double acceleration, Double maxAcceleration, String key) {
        super(key);
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
    public boolean isValid(Logger logger) {
        try {
            ConfigHelper.checkMissile(orbitDist, proximity, fuelTicks, maxSpeed, acceleration, maxAcceleration);
            ConfigHelper.checkProjExplodes2(explosionPower, explosionDamage);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }

    public Double getExplosionPower() {
        return explosionPower;
    }

    public Double getExplosionDamage() {
        return explosionDamage;
    }

    public Double getOrbitDist() {
        return orbitDist;
    }

    public Double getProximity() {
        return proximity;
    }

    public Long getFuelTicks() {
        return fuelTicks;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public Double getAcceleration() {
        return acceleration;
    }

    public Double getMaxAcceleration() {
        return maxAcceleration;
    }
}
