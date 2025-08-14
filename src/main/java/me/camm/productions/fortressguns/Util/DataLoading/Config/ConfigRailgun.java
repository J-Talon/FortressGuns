package me.camm.productions.fortressguns.Util.DataLoading.Config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

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

    static class ValidatorRailgun implements Validator<ConfigRailgun> {

        @Override
        public boolean validate(@NotNull ConfigRailgun in) {
            return true;
        }
    }
}
