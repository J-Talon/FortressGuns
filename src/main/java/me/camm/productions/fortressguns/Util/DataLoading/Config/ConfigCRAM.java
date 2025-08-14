package me.camm.productions.fortressguns.Util.DataLoading.Config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("cram")
public class ConfigCRAM implements ConfigObject {
    //this is bound to change, hence why it's not extending ConfigArtilleryGeneral

    private double health;
    private long cooldown;

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
        return new ValidatorCRAM().validate(this);
    }


    static class ValidatorCRAM implements Validator<ConfigCRAM> {
        @Override
        public boolean validate(@NotNull ConfigCRAM in) {
            return true;
        }
    }

}
