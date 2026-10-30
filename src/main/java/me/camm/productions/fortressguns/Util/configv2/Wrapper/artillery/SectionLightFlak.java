package me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery;


import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*
#Light flak
[lightFlak]
health = 15
cooldown = 500

#ammo drum size of the light flak before needing reload
#set to -1 to allow arbitrary length belt feeding
magSize = 40

#The percent chance for any one shot to jam the gun. Double value, 0 to 1.
jamPercent = 0

#the amount of heat which should be incurred per shot.
#Double value, [0-100], where 100 is max heat. If the gun is fired beyond max heat it will take damage.
overheat = 1


#the rate at which the gun cools down. Double [0-100]
heatDissipationRate = 1

#The amount of time in ticks after shooting to wait before the gun starts cooling down
inactiveHeatTicks = 2000
 */
public class SectionLightFlak extends ConfigSection {

    private Double health;
    private Long cooldown;
    private Integer magSize;
    private Double jamChance;
    private Double overheat;
    private Double dissipation;
    private Long heatTicks;
    private Boolean infinite;

    public SectionLightFlak(Double health, Long cooldown, Integer magSize, Double jamChance, Double overheat, Double headDissipation, Long heatTicks, Boolean infinite, String key) {
        super(key);
        this.health = health;
        this.cooldown = cooldown;
        this.magSize = magSize;
        this.jamChance = jamChance;
        this.overheat = overheat;
        this.dissipation = headDissipation;
        this.heatTicks = heatTicks;
        this.infinite = infinite;
    }


    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionLightFlak>() {
                @Override
                public void validate(@NotNull SectionLightFlak in) {
                    inRange(0,100, overheat, ConfigField.HEAT_IN);
                    inRange(0,100, overheat, ConfigField.HEAT_OUT);
                    atLeast(0, heatTicks, ConfigField.HEAT_TICKS);

                    notNull(magSize, ConfigField.MAG);
                    notNull(infinite, ConfigField.INF);
                    moreThan(0, health, ConfigField.HEALTH);
                    atLeast(0, jamChance, ConfigField.JAM);
                    atLeast(0, cooldown, ConfigField.CD);

                }
            }.validate(this);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }


    public Double getHealth() {
        return health;
    }

    public Long getCooldown() {
        return cooldown;
    }

    public Integer getMagSize() {
        return magSize;
    }

    public Double getJamChance() {
        return jamChance;
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

    public Boolean infinite() {
        return infinite;
    }
}
