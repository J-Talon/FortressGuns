package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema.ConfigRailgun;
import org.jetbrains.annotations.NotNull;

public class ValidatorRailgun implements Validator<ConfigRailgun> {

    @Override
    public boolean validate(@NotNull ConfigRailgun in) {
        return true;
    }
}
