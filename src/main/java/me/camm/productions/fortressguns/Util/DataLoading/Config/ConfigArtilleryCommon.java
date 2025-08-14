package me.camm.productions.fortressguns.Util.DataLoading.Config;

import org.jetbrains.annotations.NotNull;

public abstract class ConfigArtilleryCommon implements ConfigObject {

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
        ValidatorCommon gen = new ValidatorCommon();
        return gen.validate(this);
    }


    static class ValidatorCommon implements Validator<ConfigArtilleryCommon> {

        @Override
        public boolean validate(@NotNull ConfigArtilleryCommon in) {
            double health = in.getHealth();
            long cool = in.getCooldown();

            return health > 0 && cool > 0;
        }
    }
}
