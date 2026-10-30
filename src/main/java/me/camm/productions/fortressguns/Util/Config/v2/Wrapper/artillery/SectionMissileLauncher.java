package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;



/*
#Missile launcher
[missileLauncher]
health = 35
cooldown = 10000
missiles = 6

 */

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

public class SectionMissileLauncher extends ConfigSection {

    private double health;
    private long cooldown;
    private int missiles;
    private boolean infinite;

    public SectionMissileLauncher(double health, long cooldown, int missiles, boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.missiles = missiles;
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

    public int getMissiles() {
        return missiles;
    }
}
