package me.camm.productions.fortressguns.Util.Math.RayPosition;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Trajectory {
    private final Vector origin, direction;


    public Trajectory(@NotNull Vector origin, @NotNull Vector direction) {
        Vector destination = origin.clone().add(direction.clone().multiply(2));
        Vector start = origin.clone().add(direction.clone());
        this.direction = destination.clone().subtract(start.clone());
        this.origin = start;

    }


    public @Nullable Vector getHitPosition(Entity entity) {
        Location loc = entity.getLocation();
        Vector difference = loc.toVector().subtract(origin);
        double dot = direction.dot(difference);
        if (dot < 0) {
            return null;
        }

        World w = entity.getWorld();

        BoundingBox bounds = entity.getBoundingBox();
        Boxer boxer = new Boxer(direction.clone(),origin.clone());
        Vector pointXMin = boxer.getXPoint(bounds.getMinX());
        playParticle(pointXMin, w);

        Vector pointYMin = boxer.getYPoint(bounds.getMinY());
        playParticle(pointYMin, w);

        Vector pointZMin = boxer.getZPoint(bounds.getMinZ());
        playParticle(pointZMin, w);

        Vector pointXMax = boxer.getXPoint(bounds.getMaxX());
        playParticle(pointXMax, w);

        Vector pointYMax = boxer.getYPoint(bounds.getMaxY());
        playParticle(pointYMax, w);

        Vector pointZMax = boxer.getZPoint(bounds.getMaxZ());
        playParticle(pointZMax, w);

        return hit(pointXMin,pointXMax,pointYMin,pointYMax,pointZMin,pointZMax,bounds);

    }


    private void playParticle(Vector v, World w) {
        w.spawnParticle(Particle.ASH, v.getX(), v.getY(), v.getZ(), 1,0,0,0,0);
    }


//    public List<Tuple2<Entity, Vector>> getInLine(List<Entity> entities) {
//
//
//
//        List<Tuple2<Entity,Vector>> hit = new ArrayList<>();
//        for (Entity entity: entities) {
//            hit.add(new Tuple2<>(entity, hitPos));
//        }
//        return hit;
//    }



    private @Nullable Vector hit(Vector x1, Vector x2, Vector y1, Vector y2, Vector z1, Vector z2, BoundingBox box) {
        if (box.contains(x1)) return x1;
        if (box.contains(x2)) return x2;
        if (box.contains(y1)) return y1;
        if (box.contains(y1)) return y2;
        if (box.contains(z1)) return z1;
        if (box.contains(z2)) return z2;
        return null;
    }



}