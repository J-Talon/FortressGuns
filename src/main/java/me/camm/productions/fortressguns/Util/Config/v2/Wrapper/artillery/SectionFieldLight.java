package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;


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
    private double health;
    private long cooldown;
    private boolean infinite;

    public SectionFieldLight(double health, long cooldown, boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.infinite = infinite;
    }

    @Override
    public boolean isValid() {
        return false;
    }


    public boolean isInfinite() {
        return infinite;
    }

    public Double getHealth() {
        return health;
    }

    public Long getCooldown() {
        return cooldown;
    }
}
