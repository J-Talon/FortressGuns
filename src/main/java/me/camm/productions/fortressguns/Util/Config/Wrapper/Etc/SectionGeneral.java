package me.camm.productions.fortressguns.Util.Config.Wrapper.Etc;


import me.camm.productions.fortressguns.Util.Config.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.builder.ConfigField;
import me.camm.productions.fortressguns.Util.Config.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*

 */
public class SectionGeneral extends ConfigSection {

    private final Boolean enableFlares;
    private final Boolean useVanillaExplosions;
    private final Boolean destructiveExplosions;
    private final Boolean verboseChunkLogging;
    private final Boolean easyRepairs;
    private final Double repairAmount;

    public SectionGeneral(Boolean enableFlares,
                          Boolean useVanillaExplosions,
                          Boolean destructiveExplosions,
                          Boolean verboseChunkLogging,
                          Boolean easyRepairs,
                          Double repairAmount,
                          String key) {
        super(key);
        this.enableFlares = enableFlares;
        this.useVanillaExplosions = useVanillaExplosions;
        this.destructiveExplosions = destructiveExplosions;
        this.verboseChunkLogging = verboseChunkLogging;
        this.easyRepairs = easyRepairs;
        this.repairAmount = repairAmount;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {

            new Validator<SectionGeneral>() {

                @Override
                public void validate(@NotNull SectionGeneral in) {
                    notNull(enableFlares, ConfigField.FLARES);
                    notNull(useVanillaExplosions, ConfigField.VANILLA);
                    notNull(destructiveExplosions, ConfigField.DESTRUCTIVE);
                    notNull(verboseChunkLogging, ConfigField.VERBOSE_CHUNK);
                    notNull(easyRepairs, ConfigField.EASY_REPAIRS);
                    atLeast(0, repairAmount, ConfigField.REPAIR_AMOUNT);
                }
            }.validate(this);
        } catch (IllegalArgumentException e) {
            super.log(e.getMessage(), key, logger);
            return false;
        }
        return true;
    }

    public Boolean isEnableFlares() {
        return enableFlares;
    }

    public Boolean isUseVanillaExplosions() {
        return useVanillaExplosions;
    }

    public Boolean getDestructiveExplosions() {
        return destructiveExplosions;
    }

    public Boolean getVerboseChunkLogging() {
        return verboseChunkLogging;
    }


    public Boolean getEasyRepairs() {
        return easyRepairs;
    }

    public Double getRepairAmount() {
        return repairAmount;
    }
}
