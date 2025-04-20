package me.camm.productions.fortressguns.Explosions.Old;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosions.ExplosionDebris;
import me.camm.productions.fortressguns.Explosions.ExplosionShellHE;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
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
        w.createExplosion(source,x,y,z,radius, false, destructiveExplosions ? Explosion.Effect.b : Explosion.Effect.a);
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


        Vec3D nms = source.getMot();
        ExplosionDebris debris = new ExplosionDebris(x,y,z,w,radius,source,destructiveExplosions, new Vector(nms.getX(), nms.getY(), nms.getZ()), solidPenPower);
        debris.perform();

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
