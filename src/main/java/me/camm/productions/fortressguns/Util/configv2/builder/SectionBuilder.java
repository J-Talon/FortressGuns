package me.camm.productions.fortressguns.Util.configv2.builder;

import me.camm.productions.fortressguns.Util.configv2.ConfigSection;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;

@FunctionalInterface
public interface SectionBuilder<T extends ConfigSection> {
    public abstract T build(TomlParseResult result, String key)
            throws IllegalArgumentException,
            NullPointerException,
            TomlInvalidTypeException;


    static @Nullable Double getDoubleOrInt(TomlParseResult res, String key) {
        if (res.isDouble(key)) {
            return res.getDouble(key);
        }

        if (res.isLong(key)) {
            Long v = res.getLong(key);
            if (v == null) return null;

            return v.doubleValue();
        }
        return null;
    }

}
