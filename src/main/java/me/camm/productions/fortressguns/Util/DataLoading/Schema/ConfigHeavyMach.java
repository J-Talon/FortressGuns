package me.camm.productions.fortressguns.Util.DataLoading.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyMachineGun;
import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorHeavyMach;

@JsonTypeName("heavyMachineGun")
public class ConfigHeavyMach implements ConfigObject {
    private double health;
    private int magSize;
    private double jamPercent;
    private double overheat;

    private double heatDissipationRate;
    private long inactiveHeatTicks;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getMagSize() {
        return magSize;
    }

    public void setMagSize(int magSize) {
        this.magSize = magSize;
    }

    public double getJamPercent() {
        return jamPercent;
    }

    public void setJamPercent(double jamPercent) {
        this.jamPercent = jamPercent;
    }

    public double getOverheat() {
        return overheat;
    }

    public void setOverheat(double overheat) {
        this.overheat = overheat;
    }

    public double getHeatDissipationRate() {
        return heatDissipationRate;
    }

    public void setHeatDissipationRate(double heatDissipationRate) {
        this.heatDissipationRate = heatDissipationRate;
    }

    public long getInactiveHeatTicks() {
        return inactiveHeatTicks;
    }

    public void setInactiveHeatTicks(int inactiveHeatTicks) {
        this.inactiveHeatTicks = inactiveHeatTicks;
    }

    @Override
    public boolean apply() {
        boolean res = new ValidatorHeavyMach().validate(this);
        if (!res)
            return false;

        HeavyMachineGun.setJamPercent(jamPercent);
        HeavyMachineGun.setMagSize(magSize);
        HeavyMachineGun.setOverheat(overheat);
        HeavyMachineGun.setMaxHealth(health);
        HeavyMachineGun.setInactiveHeatTicks(inactiveHeatTicks);
        HeavyMachineGun.setHeatDissipation(heatDissipationRate);
        return true;
    }
}
