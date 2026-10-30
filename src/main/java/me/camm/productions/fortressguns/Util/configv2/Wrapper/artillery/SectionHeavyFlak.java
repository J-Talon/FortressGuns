package me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery;


import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
#Heavy flak

[heavyFlak]
health = 100
cooldown = 5000


 */
public class SectionHeavyFlak extends ConfigSection {

    private Double health;
    private Long cooldown;
    private Boolean infinite;

    public SectionHeavyFlak(Double health, Long cooldown, Boolean infinite, String key) {
        super(key);
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    public Boolean isInfinite() {
        return infinite;
    }

    public Double getHealth() {
        return health;
    }

    public Long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionHeavyFlak>() {

                @Override
                public void validate(@NotNull SectionHeavyFlak in) {
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
}
