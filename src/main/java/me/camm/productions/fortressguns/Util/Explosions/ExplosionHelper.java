package me.camm.productions.fortressguns.Util.Explosions;

import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;

public class ExplosionHelper {

    private static boolean destructiveExplosions = true;
    private static boolean useVanillaExplosions = false;

    public static void setDestructiveExplosions(boolean destructive) {
        destructiveExplosions = destructive;
    }

    public static void setUseVanillaExplosions(boolean useVanillaExplosions) {
        ExplosionHelper.useVanillaExplosions = useVanillaExplosions;
    }


    public static void vanillaExplosion(World w, Entity source, double x, double y, double z, float radius) {
        w.createExplosion(source,x,y,z,radius, false, destructiveExplosions ? Explosion.Effect.b : Explosion.Effect.a);
    }


    public static void heavyShellExplosion(World w, Entity source, double x, double y, double z, float radius, ProjectileExplosive explosive) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ShellExplosion e = new ShellExplosion(w,source, x,y,z,radius,new ExplosionDecoration.StandardDecoration(), explosive, destructiveExplosions);
        e.playExplosion();

    }


    public static void flakHeavyExplosion(World w, Entity source, double x, double y, double z, float radius, ProjectileExplosive explosive) {
        if (useVanillaExplosions) {
            vanillaExplosion(w,source,x,y,z,radius);
            return;
        }

        ShellExplosion e = new ShellExplosion(w,source, x,y,z,radius,new ExplosionDecoration.LargeFlakDecoration(), explosive, destructiveExplosions);
        e.playExplosion();

    }


}
