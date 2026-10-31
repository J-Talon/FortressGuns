package me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
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


    public SectionShellSolid(Double weight, Double damage, String key) {
        super(key);
        this.weight = weight;
        this.damage = damage;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            new Validator<SectionShellSolid>() {

                @Override
                public void validate(@NotNull SectionShellSolid in) {
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
