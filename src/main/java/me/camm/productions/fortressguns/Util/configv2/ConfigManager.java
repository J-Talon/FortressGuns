package me.camm.productions.fortressguns.Util.configv2;

import me.camm.productions.fortressguns.Util.configv2.Wrapper.Etc.SectionGeneral;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile.*;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery.*;
import me.camm.productions.fortressguns.Util.configv2.builder.Section;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;



public class ConfigManager {


    private static ConfigManager manager = null;
    private static final AtomicBoolean isLoaded = new AtomicBoolean(false);
    private TomlParseResult configParseResult;

    //--------

    private SectionFieldHeavy fieldHeavy = null;
    private SectionFieldLight fieldLight = null;
    private SectionHeavyFlak heavyFlak = null;
    private SectionHMG hmg = null;
    private SectionLightFlak lightFlak = null;
    private SectionMissileLauncher launcher = null;


    private SectionFlak flakShell = null;
    private SectionLightFlakBullet lightFlakBullet = null;
    private SectionHeatMissile heatMissile = null;
    private SectionHMGBullet hmgBullet = null;
    private SectionShellHE shellHE = null;
    private SectionShellSolid shellSolid = null;

    private SectionGeneral general = null;

    //-------


    public static ConfigManager getInstance() {
        if (manager == null)
            manager = new ConfigManager();
        return manager;
    }

    public void load(Logger logger, TomlParseResult res) {
        configParseResult = res;

        fieldHeavy = (SectionFieldHeavy) Section.FIELD_HEAVY.build(res,logger);
        fieldLight = (SectionFieldLight) Section.FIELD_LIGHT.build(res,logger);
        heavyFlak = (SectionHeavyFlak) Section.HEAVY_FLAK.build(res, logger);
        hmg = (SectionHMG) Section.HMG.build(res, logger);
        lightFlak = (SectionLightFlak) Section.LIGHT_FLAK.build(res, logger);
        launcher = (SectionMissileLauncher) Section.MISSILE_LAUNCHER.build(res, logger);

        flakShell = (SectionFlak) Section.BIG_FLAK_SHELL.build(res, logger);
        heatMissile = (SectionHeatMissile) Section.HEAT_MISSILE.build(res, logger);
        hmgBullet = (SectionHMGBullet) Section.HMG_BULLET.build(res, logger);
        shellHE = (SectionShellHE) Section.BIG_HE_SHELL.build(res, logger);
        shellSolid = (SectionShellSolid) Section.BIG_SOLID_SHELL.build(res, logger);
        lightFlakBullet = (SectionLightFlakBullet)Section.SMALL_FLAK_SHELL.build(res, logger);

        general = (SectionGeneral) Section.GENERAL.build(res, logger);
        isLoaded.set(true);
    }


    public @Nullable String getRawValue(String keyList) {
        if (keyList == null) return null;
        keyList = keyList.strip().replace(" ", ".");
        try {
            if (configParseResult.isString(keyList)) {
                return configParseResult.getString(keyList);
            }

            if (configParseResult.isDouble(keyList)) {
                Double d = configParseResult.getDouble(keyList);
                return d == null ? null :  d + "";
            }

            if (configParseResult.isBoolean(keyList)) {
                Boolean b = configParseResult.getBoolean(keyList);
                return b == null ? null : b + "";
            }

            if (configParseResult.isLong(keyList)) {
                Long l = configParseResult.getLong(keyList);
                return l == null ? null : l + "";
            }

            return null;
        }
        catch (IllegalArgumentException | TomlInvalidTypeException e) {
            return null;
        }
    }


    public boolean isLoaded() {
        return isLoaded.get();
    }


    public SectionFieldHeavy getFieldHeavy() {
        return fieldHeavy;
    }

    public SectionFieldLight getFieldLight() {
        return fieldLight;
    }

    public SectionHeavyFlak getHeavyFlak() {
        return heavyFlak;
    }

    public SectionHMG getHmg() {
        return hmg;
    }

    public SectionLightFlak getLightFlak() {
        return lightFlak;
    }

    public SectionMissileLauncher getLauncher() {
        return launcher;
    }

    public SectionFlak getFlakShell() {
        return flakShell;
    }

    public SectionHeatMissile getHeatMissile() {
        return heatMissile;
    }

    public SectionHMGBullet getHmgBullet() {
        return hmgBullet;
    }

    public SectionShellHE getShellHE() {
        return shellHE;
    }

    public SectionShellSolid getShellSolid() {
        return shellSolid;
    }

    public SectionGeneral getGeneral() {
        return general;
    }

    public SectionLightFlakBullet getLightFlakBullet() {
        return lightFlakBullet;
    }
}
