package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;

@FunctionalInterface
public interface SectionBuilder<T extends ConfigSection> {
    public abstract T build(TomlParseResult result, String key)
            throws IllegalArgumentException,
            NullPointerException,
            TomlInvalidTypeException;

}
