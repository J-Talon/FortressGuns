package me.camm.productions.fortressguns.Explosions.Old;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosions.ExplosionDebris;
import me.camm.productions.fortressguns.Explosions.ExplosionShellHE;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ExplosionFactory {

    private static boolean destructiveExplosions = true;
    private static boolean useVanillaExplosions = false;

    //pen power of the solid shell
    private static float solidPenPower = 4.5f;

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


    public static void heavyShellExplosion(World w, Entity source, double x, double y, double z, float radius, ProjectileExplosive explosive) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ExplosionShellHE e = new ExplosionShellHE(x,y,z,w,radius,source, true);
        e.perform();

    }


    public static void solidShellExplosion(World w, @NotNull Entity source, double x, double y, double z, float radius) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }
        Vector velocity = source.getVelocity();
        ExplosionDebris debris = new ExplosionDebris(x,y,z,w,radius,source,destructiveExplosions, velocity, solidPenPower);
        debris.perform();

    }


    public static void flakHeavyExplosion(World w, Entity source, double x, double y, double z, float radius, ProjectileExplosive explosive) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

//        ShellExplosion e = new ShellExplosion(w,source, x,y,z,radius,new ExplosionDecoration.LargeFlakDecoration(), explosive, destructiveExplosions);
//        e.playExplosion();

    }


}
