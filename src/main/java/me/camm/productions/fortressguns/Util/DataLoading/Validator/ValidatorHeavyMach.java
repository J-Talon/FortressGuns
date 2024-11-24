package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema.ConfigHeavyMach;
import org.jetbrains.annotations.NotNull;

public class ValidatorHeavyMach implements Validator<ConfigHeavyMach> {
    @Override
    public boolean validate(@NotNull ConfigHeavyMach in) {
        double jam = in.getJampercent(), overheat = in.getOverheat(), health = in.getHealth();
        int mag = in.getMagsize();


        return (jam >= 0 && jam < 1) && (overheat >= 0 && overheat < 100) && health > 0 && mag > 0;
    }
}
