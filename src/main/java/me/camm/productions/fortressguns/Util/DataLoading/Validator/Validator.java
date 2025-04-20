package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Validator<T> {
    public boolean validate(@NotNull T in);
}
