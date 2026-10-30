package me.camm.productions.fortressguns.Util.Config;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Validator<T> {
    boolean validate(@NotNull T in);
}
