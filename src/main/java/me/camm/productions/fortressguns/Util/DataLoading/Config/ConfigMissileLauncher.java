package me.camm.productions.fortressguns.Util.DataLoading.Config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.MissileLauncher;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("missileLauncher")
public class ConfigMissileLauncher implements ConfigObject {

    private double health;
    private long cooldown;
    private int missiles;

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

    public int getMissiles() {
        return missiles;
    }

    public void setMissiles(int missiles) {
        this.missiles = missiles;
    }

    @Override
    public boolean apply() {
        boolean res = new ValidatorMissileLauncher().validate(this);
        if (!res)
            return false;

        MissileLauncher.setCooldown(cooldown);
        MissileLauncher.setMaxHealth(health);
        MissileLauncher.setMaxRockets(missiles);
        return true;

    }

    static class ValidatorMissileLauncher implements Validator<ConfigMissileLauncher> {

        @Override
        public boolean validate(@NotNull ConfigMissileLauncher in) {
            return in.getMissiles() > 0 && in.getHealth() > 0 && in.getCooldown() > 0;
        }
    }
}
