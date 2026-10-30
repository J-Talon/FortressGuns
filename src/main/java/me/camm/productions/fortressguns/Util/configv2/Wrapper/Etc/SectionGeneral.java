package me.camm.productions.fortressguns.Util.configv2.Wrapper.Etc;


import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/*

 */
public class SectionGeneral extends ConfigSection {

    private Boolean enableFlares;
    private Boolean useVanillaExplosions;
    private Boolean destructiveExplosions;


    public SectionGeneral(Boolean enableFlares, Boolean useVanillaExplosions, Boolean destructiveExplosions, String key) {
        super(key);
        this.enableFlares = enableFlares;
        this.useVanillaExplosions = useVanillaExplosions;
        this.destructiveExplosions = destructiveExplosions;
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
}
