package me.camm.productions.fortressguns.Util;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Tracer
{
    private final Vector direction;
    private final Vector origin;
    private double strength;
    private final World world;

    public Tracer(Vector direction, Vector origin, World world) {
        this.direction = direction;
        this.origin = origin;
        strength = Math.random()*10 + 3;
        this.world = world;
    }

    public Vector getPosition(double blocks)
    {
        return direction.clone().multiply(blocks).add(origin.clone());
    }

    public Vector getDirection() {
        return direction;
    }

    public HashSet<Block> breakBlocks(){

        double dist = 0;
        HashSet<Block> broken = new HashSet<>();
        while (strength > 0 && dist < 3) {
            dist += 0.5;
            Block block = world.getBlockAt(getPosition(dist).toLocation(world));
            if (conflict(block))
                broken.add(block);
        }
        return broken;
    }

    public boolean conflict(Block block){
        double strength = block.getType().getBlastResistance();
        this.strength -= strength;
        return this.strength > 0;
    }



}
