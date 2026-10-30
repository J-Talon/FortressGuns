package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery;


import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
#Light flak
[lightFlak]
health = 15
cooldown = 500

#ammo drum size of the light flak before needing reload
#set to -1 to allow arbitrary length belt feeding
magSize = 40

#The percent chance for any one shot to jam the gun. float value, 0 to 1.
jamPercent = 0

#the amount of heat which should be incurred per shot.
#float value, [0-100], where 100 is max heat. If the gun is fired beyond max heat it will take damage.
overheat = 1


#the rate at which the gun cools down. float [0-100]
heatDissipationRate = 1

#The amount of time in ticks after shooting to wait before the gun starts cooling down
inactiveHeatTicks = 2000
 */
public class SectionLightFlak extends ConfigSection {

    private double health;
    private long cooldown;
    private int magSize;
    private float jamChance;
    private float overheat;
    private float headDissipation;
    private long heatTicks;
    private boolean infinite;

    public SectionLightFlak(double health, long cooldown, int magSize, float jamChance, float overheat, float headDissipation, long heatTicks, boolean infinite) {
        this.health = health;
        this.cooldown = cooldown;
        this.magSize = magSize;
        this.jamChance = jamChance;
        this.overheat = overheat;
        this.headDissipation = headDissipation;
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

    public long getCooldown() {
        return cooldown;
    }

    public int getMagSize() {
        return magSize;
    }

    public float getJamChance() {
        return jamChance;
    }

    public float getOverheat() {
        return overheat;
    }

    public float getHeadDissipation() {
        return headDissipation;
    }

    public long getHeatTicks() {
        return heatTicks;
    }
}
