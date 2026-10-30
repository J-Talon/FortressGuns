package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

import java.util.logging.Logger;


/*
[fieldLight]
#healthpoints of the artillery
health = 20
#The cooldown time to wait until the artillery can fire again, if reloading is disabled
#if cooldown is disabled the firing speed is still gated by the time required to play out
#the current animation and current shot being fired. Time in milliseconds.
cooldown = 3000

 */
public class SectionFieldLight extends ConfigSection {
    private Double health;
    private Long cooldown;
    private Boolean infinite;

    public SectionFieldLight(Double health, Long cooldown, Boolean infinite, String key) {
        super(key);
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            ConfigHelper.checkHCDI(health, cooldown, infinite);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
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
}
