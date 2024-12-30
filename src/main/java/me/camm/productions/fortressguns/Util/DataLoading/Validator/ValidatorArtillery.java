package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigArtilleryGeneral;
import org.jetbrains.annotations.NotNull;

public class ValidatorArtillery implements Validator<ConfigArtilleryGeneral> {

    @Override
    public boolean validate(@NotNull ConfigArtilleryGeneral in) {
        double health = in.getHealth();
        long cool = in.getCooldown();

        return health > 0 && cool > 0;
    }
}
