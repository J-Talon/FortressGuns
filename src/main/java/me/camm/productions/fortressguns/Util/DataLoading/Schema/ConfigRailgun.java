package me.camm.productions.fortressguns.Util.DataLoading.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("railGun")
public class ConfigRailgun implements ConfigObject {
    private double health;
    private long cooldown;
    private int range;
    private double maxdamage;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getMaxdamage() {
        return maxdamage;
    }

    public void setMaxdamage(double maxdamage) {
        this.maxdamage = maxdamage;
    }

    @Override
    public boolean apply() {
        return true;
    }
}
