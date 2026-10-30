package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;


import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

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

    public SectionHeavyFlak(Double health, Long cooldown, Boolean infinite) {
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
    public boolean isValid() {
        try {
            ConfigHelper.checkHCDI(health, cooldown, infinite);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
