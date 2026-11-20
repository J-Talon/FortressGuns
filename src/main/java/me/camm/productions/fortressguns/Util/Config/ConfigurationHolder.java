package me.camm.productions.fortressguns.Util.Config;

import org.jetbrains.annotations.Nullable;

public interface ConfigurationHolder<T extends ConfigSection> {
    public void setStaticDefaults(@Nullable T data);

}
