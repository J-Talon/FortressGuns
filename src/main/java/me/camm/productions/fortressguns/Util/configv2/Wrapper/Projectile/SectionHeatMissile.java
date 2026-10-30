package me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

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
            new Validator<SectionHeatMissile>() {
                @Override
                public void validate(@NotNull SectionHeatMissile in) {
                    atLeast(0, orbitDist, ConfigField.ORBIT);
                    atLeast(0, proximity, ConfigField.PROX);
                    atLeast(0, fuelTicks, ConfigField.FUEL);
                    atLeast(0, maxSpeed,ConfigField.MAX_SPD);
                    atLeast(0, acceleration, ConfigField.ACC);
                    atLeast(0, maxAcceleration, ConfigField.MAX_ACC);

                    atLeast(0, explosionDamage, ConfigField.ED);
                    atLeast(0, explosionPower, ConfigField.EP);

                }
            }.validate(this);

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
