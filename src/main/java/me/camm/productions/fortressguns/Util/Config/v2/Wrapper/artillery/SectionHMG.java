package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;


/*

#Heavy machine gun

[heavyMachineGun]
health = 15

#ammo drum size of the machine gun before needing reload.
#set to -1 to allow arbitrary length belt feeding
magSize = -1

#The percent chance for any one shot to jam the gun. float value, [0-1]
jamPercent = 0.01

#the amount of heat which should be incurred per shot.
#float value, [0-100], where 100 is max heat. If the gun is fired beyond max heat it will take damage.
overheat = 0.8

#the rate at which the gun cools down. float [0-100]
heatDissipationRate = 1

#The amount of time in milliseconds after shooting to wait before the gun starts cooling down
inactiveHeatTicks = 2000

 */


public class SectionHMG extends ConfigSection {

    private double health;
    private int magSize;
    private float jamPercent;
    private float overheat;
    private float dissipation;
    private float heatTicks;
    private boolean infinite;

    public SectionHMG(double health, int magSize, float jamPercent, float overheat, float dissipation, float heatTicks, boolean infinite) {
        this.health = health;
        this.magSize = magSize;
        this.jamPercent = jamPercent;
        this.overheat = overheat;
        this.dissipation = dissipation;
        this.heatTicks = heatTicks;
        this.infinite = infinite;
    }


    @Override
    public boolean isValid() {
        return false;
    }


    public double getHealth() {
        return health;
    }

    public int getMagSize() {
        return magSize;
    }

    public float getJamPercent() {
        return jamPercent;
    }

    public float getOverheat() {
        return overheat;
    }

    public float getDissipation() {
        return dissipation;
    }

    public float getHeatTicks() {
        return heatTicks;
    }
}
