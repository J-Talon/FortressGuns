package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;


import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
#Heavy flak

[heavyFlak]
health = 100
cooldown = 5000


 */
public class SectionHeavyFlak extends ConfigSection {

    private double health;
    private long cooldown;
    private boolean infinite;

    public SectionHeavyFlak(double health, long cooldown, boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public double getHealth() {
        return health;
    }

    public long getCooldown() {
        return cooldown;
    }
}
