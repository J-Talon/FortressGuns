package me.camm.productions.fortressguns.Util;

import net.minecraft.util.Tuple;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Tracer
{
    private final Vector direction;
    private final Vector origin;
    private double strength;
    private final World world;

    private final int limit;

    public Tracer(Vector direction, Vector origin, World world) {
        this.direction = direction;
        this.origin = origin;
        strength = Math.random()*10 + 3;
        this.world = world;
        this.limit = Integer.MAX_VALUE;
    }

    public Tracer(Vector direction, Vector origin, World world, int limit) {
        this.direction = direction;
        this.origin = origin;
        strength = Math.random()*10 + 3;
        this.world = world;
        this.limit = limit;
    }

    public Vector getPosition(double blocks)
    {
        return direction.clone().multiply(blocks).add(origin.clone());
    }

    public Vector getDirection() {
        return direction;
    }

    public Set<Tuple<Block, Double>> getBrokenBlocks(){


        double dist = 0;
        HashSet<Tuple<Block, Double>> broken = new HashSet<>();
        int added = 0;
        while (strength > 0 && dist < 3 && added < limit) {
            dist += 0.5;
            Block block = world.getBlockAt(getPosition(dist).toLocation(world));

            if (block.getType().isAir())
                continue;


            if (conflict(block)) {
                broken.add(new Tuple<>(block, dist));
                added ++;
            }

            if (added >= limit)
                break;
        }
        return broken;
    }

    public boolean conflict(Block block){
        double strength = block.getType().getBlastResistance();
        this.strength -= strength;
        return this.strength > 0;
    }



}
