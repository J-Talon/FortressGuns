package me.camm.productions.fortressguns.Explosion;

import me.camm.productions.fortressguns.Explosion.Explosions.Ambient.ExplosionSplash;
import me.camm.productions.fortressguns.Explosion.Explosions.Ambient.ExplosionSplashLarge;
import me.camm.productions.fortressguns.Explosion.Explosions.Functional.*;
import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.Etc.SectionGeneral;
import me.camm.productions.fortressguns.Util.configv2.ConfigurationHolder;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExplosionFactory implements ConfigurationHolder<SectionGeneral> {

    private static boolean destructiveExplosions = true;
    private static boolean useVanillaExplosions = false;

    private static boolean configApplied = false;


    private static ExplosionFactory instance = null;


    private ExplosionFactory() {

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (manager.isLoaded()) return;
        setStaticDefaults(manager.getGeneral());
    }

    public static ExplosionFactory get() {
        if (instance == null) instance = new ExplosionFactory();
        return instance;
    }


    @Override
    public void setStaticDefaults(@Nullable SectionGeneral data) {
        configApplied = true;
        if (data == null) return;
        useVanillaExplosions = data.isUseVanillaExplosions();
        destructiveExplosions = data.getDestructiveExplosions();
    }



    public void vanillaExplosion(World w, Entity source, double x, double y, double z, float radius) {
        w.createExplosion(x,y,z,radius,false,destructiveExplosions, source);
    }


    public void heavyShellExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionShellHE e = new ExplosionShellHE(x,y,z,w,radius,source, destructiveExplosions);
        e.perform();

    }


    public void solidShellExplosion(World w, @NotNull Entity source, double x, double y, double z, float radius, Vector direction, @Nullable Block context) {

        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionDebris debris = new ExplosionDebris(x,y,z,w,radius,source, direction, context);
        debris.perform();

    }


    public void flakHeavyExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionFlakLarge explosion = new ExplosionFlakLarge(x,y,z,w,radius,source);
        explosion.perform();
    }

    public void flakLightExplosion(World w, Entity source, Player shooter, double x, double y, double z, @Nullable Entity ignore) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,1);
            return;
        }

        ExplosionFlakSmall small = new ExplosionFlakSmall(x,y,z,w,1, source, shooter, ignore);
        small.perform();
    }


    public void missileExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }
        ExplosionFuel fuel = new ExplosionFuel(x,y,z,w,radius, source, destructiveExplosions);
        fuel.perform();
    }


    public void smallSplashExplosion(World w, double x, double y, double z) {
        ExplosionSplash splash = new ExplosionSplash(x,y,z, w);
        splash.perform();
    }


    public static void largeSplashExplosion(World w, double x, double y, double z) {
        ExplosionSplashLarge splash = new ExplosionSplashLarge(x,y,z,w);
        splash.perform();

    }


}
