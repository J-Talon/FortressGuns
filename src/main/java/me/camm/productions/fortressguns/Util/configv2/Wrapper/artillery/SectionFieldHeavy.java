package me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery;


/*
[fieldHeavy]
health = 80
cooldown = 5000
 */

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SectionFieldHeavy extends ConfigSection {


    private Double health;
    private Long cooldown;
    private Boolean infinite;

    public SectionFieldHeavy(Double health, Long cooldown, Boolean infinite, String key) {
        super(key);
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            new Validator<SectionFieldHeavy>() {
                @Override
                public void validate(@NotNull SectionFieldHeavy in) {
                    moreThan(0, health, ConfigField.HEALTH);
                    moreThan(0, cooldown, ConfigField.CD);
                    notNull(infinite, ConfigField.INF);
                }
            }.validate(this);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }


    public Double getHealth() {
        return health;
    }

    public Long getCooldown() {
        return cooldown;
    }

    public Boolean isInfinite() {
        return infinite;
    }
}
