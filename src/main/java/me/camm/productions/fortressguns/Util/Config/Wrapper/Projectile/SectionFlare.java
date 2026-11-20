package me.camm.productions.fortressguns.Util.Config.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.ConfigSection;

import me.camm.productions.fortressguns.Util.Config.builder.ConfigField;
import me.camm.productions.fortressguns.Util.Config.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SectionFlare extends ConfigSection {

    private final Boolean enableFlares;
    private final Double flareSuccess;
    private final Double flareEffectRange;
    private final Long lifespan;
    private final Double variance;

    public SectionFlare(String key, Boolean enableFlares, Double flareSuccess, Double flareEffectRange, Long lifespan, Double variance) {
        super(key);
        this.enableFlares = enableFlares;
        this.flareSuccess = flareSuccess;
        this.flareEffectRange = flareEffectRange;
        this.lifespan = lifespan;
        this.variance = variance;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionFlare>() {
                @Override
                public void validate(@NotNull SectionFlare in) {
                    notNull(enableFlares, ConfigField.FLARE_ENABLE);
                    inRange(0, 1, flareSuccess, ConfigField.FLARE_SUCCESS);
                    atLeast(0, flareEffectRange, ConfigField.FLARE_RANGE);
                    atLeast(0, lifespan, ConfigField.FLARE_LIFE);
                    inRange(0,1, variance, ConfigField.FLARE_VAR);
                }

            }.validate(this);

        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }


    public Boolean getEnableFlares() {
        return enableFlares;
    }

    public Double getFlareSuccess() {
        return flareSuccess;
    }

    public Double getFlareEffectRange() {
        return flareEffectRange;
    }

    public Long getLifespan() {
        return lifespan;
    }

    public Double getVariance() {
        return variance;
    }
}


/*
enableFlares = true
#whether to enable flares to help players survive missiles

flareSuccessChance = 0.37
# chance for a single flare to distract missiles float [0-1]

flareEffectRange = 50
# area of effect for flares in blocks

lifeSpan = 300
# lifespan of flares in ticks

directionalVariance = 0.5
# directional variance of flares
#==============================
 */
