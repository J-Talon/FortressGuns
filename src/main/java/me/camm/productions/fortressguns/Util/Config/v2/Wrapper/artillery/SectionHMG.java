package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

import java.util.logging.Logger;


/*

#Heavy machine gun

[heavyMachineGun]
health = 15

#ammo drum size of the machine gun before needing reload.
#set to -1 to allow arbitrary length belt feeding
magSize = -1

#The percent chance for any one shot to jam the gun. Double value, [0-1]
jamPercent = 0.01

#the amount of heat which should be incurred per shot.
#Double value, [0-100], where 100 is max heat. If the gun is fired beyond max heat it will take damage.
overheat = 0.8

#the rate at which the gun cools down. Double [0-100]
heatDissipationRate = 1

#The amount of time in milliseconds after shooting to wait before the gun starts cooling down
inactiveHeatTicks = 2000

 */


public class SectionHMG extends ConfigSection {

    private Double health;
    private Integer magSize;
    private Double jamPercent;
    private Double overheat;
    private Double dissipation;
    private Long heatTicks;
    private Boolean infinite;

    public SectionHMG(Double health, Integer magSize, Double jamPercent, Double overheat, Double dissipation, Long heatTicks, Boolean infinite, String key) {
        super(key);
        this.health = health;
        this.magSize = magSize;
        this.jamPercent = jamPercent;
        this.overheat = overheat;
        this.dissipation = dissipation;
        this.heatTicks = heatTicks;
        this.infinite = infinite;
    }


    @Override
    public boolean isValid(Logger logger) {
        try {
            ConfigHelper.checkHeats(overheat, dissipation, heatTicks);
            ConfigHelper.checkMI(magSize, infinite);
            ConfigHelper.checkHealth(health);
            ConfigHelper.checkJam(jamPercent);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }


    public Double getHealth() {
        return health;
    }

    public Integer getMagSize() {
        return magSize;
    }

    public Double getJamPercent() {
        return jamPercent;
    }

    public Double getOverheat() {
        return overheat;
    }

    public Double getDissipation() {
        return dissipation;
    }

    public Long getHeatTicks() {
        return heatTicks;
    }

    public Boolean getInfinite() {
        return infinite;
    }
}
