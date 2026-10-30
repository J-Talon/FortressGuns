package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;


/*
[fieldHeavy]
health = 80
cooldown = 5000
 */

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

public class SectionFieldHeavy extends ConfigSection {


    private Double health;
    private Long cooldown;
    private Boolean infinite;

    public SectionFieldHeavy(Double health, Long cooldown, Boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid() {
        try {
            ConfigHelper.checkHCDI(health, cooldown, infinite);
        } catch (IllegalArgumentException e) {
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
