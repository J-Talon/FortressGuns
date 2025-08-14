package me.camm.productions.fortressguns.Util.DataLoading.Config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.LightFlak;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("lightFlak")
public class ConfigLightFlak implements ConfigObject {

    private double health;
    private int magSize;
    private double jamPercent;
    private double overheat;
    private long cooldown;

    private long inactiveHeatTicks;
    private double heatDissipationRate;

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

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getInactiveHeatTicks() {
        return inactiveHeatTicks;
    }

    public void setInactiveHeatTicks(long inactiveHeatTicks) {
        this.inactiveHeatTicks = inactiveHeatTicks;
    }

    public double getHeatDissipationRate() {
        return heatDissipationRate;
    }

    public void setHeatDissipationRate(double heatDissipationRate) {
        this.heatDissipationRate = heatDissipationRate;
    }

    @Override
    public boolean apply() {
        boolean res = new ValidatorLightFlak().validate(this);
        if (!res)
            return false;

        LightFlak.setMagSize(magSize);
        LightFlak.setCooldown(cooldown);
        LightFlak.setOverheat(overheat);
        LightFlak.setJamPercent(jamPercent);
        LightFlak.setInactiveHeatTicks(inactiveHeatTicks);
        LightFlak.setMaxHealth(health);
        return true;

    }

    static class ValidatorLightFlak implements Validator<ConfigLightFlak> {
        @Override
        public boolean validate(@NotNull ConfigLightFlak in) {

            long cool = in.getCooldown();
            double jam = in.getJamPercent(), health = in.getHealth(), overheat = in.getOverheat(), heatOut = in.getHeatDissipationRate();
            long inactive = in.getInactiveHeatTicks();

            return (cool > 0) && (jam >= 0 && jam <= 1) &&
                    (health > 0) && (overheat >= 0 && overheat < 100) &&
                    (inactive >= 0) &&
                    (heatOut >= 0 && heatOut <= 100);

        }
    }
}
