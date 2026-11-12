package me.camm.productions.fortressguns.Util.config.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.config.ConfigSection;
import me.camm.productions.fortressguns.Util.config.builder.ConfigField;
import me.camm.productions.fortressguns.Util.config.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
[solidShell]
weight = 1.2
damage = 25
 */
public class SectionShellSolid extends ConfigSection {

    private Double weight;
    private Double damage;
    private Double explosionDamage;
    private Double explosionRad;
    private Double angle;
    private Double penPower;

    public SectionShellSolid(Double weight, Double damage, Double explosionDamage, Double explosionRad, Double angle, Double penPower, String key) {
        super(key);
        this.weight = weight;
        this.damage = damage;
        this.explosionDamage = explosionDamage;
        this.explosionRad = explosionRad;
        this.angle = angle;
        this.penPower = penPower;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            new Validator<SectionShellSolid>() {

                @Override
                public void validate(@NotNull SectionShellSolid in) {
                    atLeast(0, weight, ConfigField.WEIGHT);
                    atLeast(0, damage, ConfigField.DAM);
                    atLeast(0, explosionDamage, ConfigField.ED);
                    atLeast(0, explosionRad, ConfigField.EP);
                    atLeast(0, angle, ConfigField.ANGLE);
                    moreThan(0, penPower, ConfigField.PEN);

                }
            }.validate(this);

        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getDamage() {
        return damage;
    }

    public Double getExplosionDamage() {
        return explosionDamage;
    }

    public Double getExplosionRad() {
        return explosionRad;
    }

    public Double getAngle() {
        return angle;
    }

    public Double getPenPower() {
        return penPower;
    }
}
