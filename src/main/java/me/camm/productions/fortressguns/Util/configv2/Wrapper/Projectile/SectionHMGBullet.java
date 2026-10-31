package me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
[HMGBullet]
weight = 0.1
damage = 5

 */
public class SectionHMGBullet extends ConfigSection {

    private Double weight;
    private Double damage;

    public SectionHMGBullet(Double weight, Double damage, String key) {
        super(key);
        this.weight = weight;
        this.damage = damage;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionHMGBullet>() {

                @Override
                public void validate(@NotNull SectionHMGBullet in) {
                    atLeast(0, weight, ConfigField.WEIGHT);
                    atLeast(0, damage, ConfigField.DAM);

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
}
