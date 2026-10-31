package me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
[shellHE]
explosionPower = 4
explosionDamage = 25
damage = 10

 */
public class SectionShellHE extends ConfigSection {

    private Double explosionPower;
    private Double explosionDamage;
    private Double damage;

    public SectionShellHE(Double explosionPower, Double explosionDamage, Double damage, String key) {
        super(key);
        this.explosionPower = explosionPower;
        this.explosionDamage = explosionDamage;
        this.damage = damage;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionShellHE>() {
                @Override
                public void validate(@NotNull SectionShellHE in) {
                    atLeast(0, explosionDamage, ConfigField.ED);
                    atLeast(0, explosionPower, ConfigField.EP);
                    atLeast(0, damage, ConfigField.DAM);
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

    public Double getDamage() {
        return damage;
    }
}
