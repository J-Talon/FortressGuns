package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;



/*
#Missile launcher
[missileLauncher]
health = 35
cooldown = 10000
missiles = 6

 */

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

public class SectionFlak extends ConfigSection {

    private Double health;
    private Long cooldown;
    private Integer missiles;
    private Boolean infinite;

    public SectionFlak(Double health, Long cooldown, Integer missiles, Boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.missiles = missiles;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid() {
        try {
            ConfigHelper.checkHCDI(health, cooldown, infinite);
            ConfigHelper.checkMI(missiles, infinite);
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

    public Integer getMissiles() {
        return missiles;
    }
}
