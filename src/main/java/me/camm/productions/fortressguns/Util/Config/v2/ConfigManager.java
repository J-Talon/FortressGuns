package me.camm.productions.fortressguns.Util.Config.v2;

import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Etc.SectionGeneral;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile.*;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.*;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.SectionFlak;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;



public class ConfigManager {


    private static ConfigManager manager = null;
    private static final AtomicBoolean isLoaded = new AtomicBoolean(false);

    //--------

    private SectionFieldHeavy fieldHeavy = null;
    private SectionFieldLight fieldLight = null;
    private SectionHeavyFlak heavyFlak = null;
    private SectionHMG hmg = null;
    private SectionLightFlak lightFlak = null;
    private SectionFlak launcher = null;


    private me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile.SectionFlak flakShell = null;
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

    public void load(Plugin plugin, Logger logger) {



    }


    public boolean isLoaded() {
        return isLoaded.get();
    }

}
