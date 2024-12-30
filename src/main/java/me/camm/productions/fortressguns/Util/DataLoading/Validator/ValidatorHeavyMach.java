package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigHeavyMach;
import org.jetbrains.annotations.NotNull;

public class ValidatorHeavyMach implements Validator<ConfigHeavyMach> {
    @Override
    public boolean validate(@NotNull ConfigHeavyMach in) {
        double jam = in.getJamPercent(), overheat = in.getOverheat(), health = in.getHealth(), heatOut = in.getHeatDissipationRate();
        int mag = in.getMagSize();
        long inactive = in.getInactiveHeatTicks();


        return (jam >= 0 && jam < 1) &&
                (overheat >= 0 && overheat < 100) &&
                health > 0 && mag > 0 &&
                inactive >=0 &&
                (heatOut >=0 && heatOut <= 100);
    }
}
