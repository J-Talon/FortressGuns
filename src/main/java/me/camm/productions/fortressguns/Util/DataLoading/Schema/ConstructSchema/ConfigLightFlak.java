package me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.LightFlak;
import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorLightFlak;

@JsonTypeName("lightFlak")
public class ConfigLightFlak implements ConfigObject {

    private double health;
    private int magsize;
    private double jampercent;
    private double overheat;
    private long cooldown;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getMagsize() {
        return magsize;
    }

    public void setMagsize(int magsize) {
        this.magsize = magsize;
    }

    public double getJampercent() {
        return jampercent;
    }

    public void setJampercent(double jampercent) {
        this.jampercent = jampercent;
    }

    public double getOverheat() {
        return overheat;
    }

    public void setOverheat(double overheat) {
        this.overheat = overheat;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean apply() {
        boolean res = new ValidatorLightFlak().validate(this);
        if (!res)
            return false;

        LightFlak.setMagSize(magsize);
        LightFlak.setCooldown(cooldown);
        LightFlak.setOverheat(overheat);
        LightFlak.setJamPercent(jampercent);
        LightFlak.setMaxHealth(health);
        return true;

    }
}
