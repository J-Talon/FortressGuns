package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigLightFlak;
import org.jetbrains.annotations.NotNull;

public class ValidatorLightFlak implements Validator<ConfigLightFlak> {
    @Override
    public boolean validate(@NotNull ConfigLightFlak in) {

        long cool = in.getCooldown();
        double jam = in.getJamPercent(), health = in.getHealth(), overheat = in.getOverheat(), heatOut = in.getHeatDissipationRate();
        long inactive = in.getInactiveHeatTicks();

        return (cool > 0) && (jam >=0 && jam <= 1) &&
                (health > 0) && (overheat >= 0 && overheat < 100) &&
                (inactive >=0 ) &&
                (heatOut >= 0 && heatOut <= 100);

    }
}
