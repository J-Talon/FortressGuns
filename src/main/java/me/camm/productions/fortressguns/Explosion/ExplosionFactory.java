package me.camm.productions.fortressguns.Explosion;

import me.camm.productions.fortressguns.Explosion.Explosions.Ambient.ExplosionSplash;
import me.camm.productions.fortressguns.Explosion.Explosions.Ambient.ExplosionSplashLarge;
import me.camm.productions.fortressguns.Explosion.Explosions.Functional.ExplosionDebris;
import me.camm.productions.fortressguns.Explosion.Explosions.Functional.ExplosionFlakLarge;
import me.camm.productions.fortressguns.Explosion.Explosions.Functional.ExplosionShellHE;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExplosionFactory {

    private static boolean destructiveExplosions = true;
    private static boolean useVanillaExplosions = false;

    public static void setDestructiveExplosions(boolean destructive) {
        destructiveExplosions = destructive;
    }

    public static boolean allowDestructiveExplosions() {
        return destructiveExplosions;
    }

    public static void setUseVanillaExplosions(boolean useVanillaExplosions) {
        ExplosionFactory.useVanillaExplosions = useVanillaExplosions;
    }


    public static void vanillaExplosion(World w, Entity source, double x, double y, double z, float radius) {
        w.createExplosion(x,y,z,radius,false,destructiveExplosions, source);
    }


    public static void heavyShellExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionShellHE e = new ExplosionShellHE(x,y,z,w,radius,source, true);
        e.perform();

    }


    public static void solidShellExplosion(World w, @NotNull Entity source, double x, double y, double z, float radius, Vector direction, @Nullable Block context) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionDebris debris = new ExplosionDebris(x,y,z,w,radius,source,destructiveExplosions, direction, context);
        debris.perform();

    }


    public static void flakHeavyExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionFlakLarge explosion = new ExplosionFlakLarge(x,y,z,w,radius,source);
        explosion.perform();
    }

    public static void flakLightExplosion(World w, Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
        }
    }


    public static void smallSplashExplosion(World w, double x, double y, double z) {
        ExplosionSplash splash = new ExplosionSplash(x,y,z, w);
        splash.perform();
    }


    public static void largeSplashExplosion(World w, double x, double y, double z) {
        ExplosionSplashLarge splash = new ExplosionSplashLarge(x,y,z,w);
        splash.perform();

    }


}
