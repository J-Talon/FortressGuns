package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;


/*
[fieldHeavy]
health = 80
cooldown = 5000
 */


import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

public class SectionFieldHeavy extends ConfigSection {


    private double health;
    private long cooldown;
    private boolean infinite;

    public SectionFieldHeavy(double health, long cooldown, boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid() {
        return false;
    }


    public double getHealth() {
        return health;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isInfinite() {
        return infinite;
    }
}
