package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Validator<T> {
    boolean validate(@NotNull T in);
}
