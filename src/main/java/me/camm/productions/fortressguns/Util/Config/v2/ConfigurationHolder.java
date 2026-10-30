package me.camm.productions.fortressguns.Util.Config.v2;

import org.jetbrains.annotations.Nullable;

public interface ConfigurationHolder<T extends ConfigSection> {
    public void setStaticDefaults(@Nullable T data);

}
