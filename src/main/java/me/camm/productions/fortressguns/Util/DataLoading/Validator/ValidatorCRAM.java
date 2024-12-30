package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigCRAM;
import org.jetbrains.annotations.NotNull;

public class ValidatorCRAM implements Validator<ConfigCRAM> {
    @Override
    public boolean validate(@NotNull ConfigCRAM in) {
        return true;
    }
}
