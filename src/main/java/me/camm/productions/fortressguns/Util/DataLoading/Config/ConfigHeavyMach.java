package me.camm.productions.fortressguns.Util.DataLoading.Config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyMachineGun;
import org.jetbrains.annotations.NotNull;

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

    static class ValidatorHeavyMach implements Validator<ConfigHeavyMach> {
        @Override
        public boolean validate(@NotNull ConfigHeavyMach in) {
            double jam = in.getJamPercent(), overheat = in.getOverheat(), health = in.getHealth(), heatOut = in.getHeatDissipationRate();
            long inactive = in.getInactiveHeatTicks();


            return (jam >= 0 && jam <= 1) &&
                    (overheat >= 0 && overheat < 100) &&
                    health > 0 &&
                    inactive >= 0 &&
                    (heatOut >= 0 && heatOut <= 100);
        }
    }
}
