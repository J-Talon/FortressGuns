package me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema;

import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorGeneral;

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
        ValidatorGeneral gen = new ValidatorGeneral();
        return gen.validate(this);
    }
}
