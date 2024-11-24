package me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyMachineGun;
import me.camm.productions.fortressguns.Util.DataLoading.Validator.Validator;
import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorHeavyMach;

@JsonTypeName("heavyMachineGun")
public class ConfigHeavyMach implements ConfigObject {
    private double health;
    private int magsize;
    private double jampercent;
    private double overheat;

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

    @Override
    public boolean apply() {
        boolean res = new ValidatorHeavyMach().validate(this);
        if (!res)
            return false;

        HeavyMachineGun.setJamPercent(jampercent);
        HeavyMachineGun.setMagSize(magsize);
        HeavyMachineGun.setOverheat(overheat);
        HeavyMachineGun.setMaxHealth(health);
        return true;
    }
}
