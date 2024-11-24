package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema.ConfigMissileLauncher;
import org.jetbrains.annotations.NotNull;

public class ValidatorMissileLauncher implements Validator<ConfigMissileLauncher> {

    @Override
    public boolean validate(@NotNull ConfigMissileLauncher in) {
     return in.getMissiles() > 0 && in.getHealth() > 0 && in.getCooldown() > 0;
    }
}
