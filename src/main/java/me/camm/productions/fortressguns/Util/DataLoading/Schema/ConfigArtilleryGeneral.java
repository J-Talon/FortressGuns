package me.camm.productions.fortressguns.Util.DataLoading.Schema;

import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorArtillery;

public abstract class ConfigArtilleryGeneral implements ConfigObject {

    protected double health;
    protected long cooldown;

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

    @Override
    public boolean apply() {
        ValidatorArtillery gen = new ValidatorArtillery();
        return gen.validate(this);
    }
}
