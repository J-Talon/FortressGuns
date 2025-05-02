package me.camm.productions.fortressguns.Explosions.Abstract;


import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Optional;

public abstract class Allocator<R, I> {
    //return: R
    //input: I


    protected double resolution;
    protected World world;
    protected Vector position;

    public Allocator(World nmsWorld, Vector position, double resolution) {
        this.resolution = resolution;
        this.world = nmsWorld;
        this.position = position;
    }

    public Allocator(World nmsWorld, Vector position) {
        this(nmsWorld, position, 0.3d);
    }

    protected World getBukkitWorld() {
        return world;
    }

    protected Location getBukkitOrigin() {
     return new Location(getBukkitWorld(), position.getX(), position.getY(), position.getZ());
    }

    public World getWorld() {
        return world;
    }

    public Vector getPosition() {
        return position;
    }


    public abstract R allocate(I inputContext);

    public Optional<Float> getBlockResistance(IBlockData blockData, Fluid fluid) {
        return blockData.isAir() && fluid.isEmpty() ?
                Optional.empty() :
                Optional.of(Math.max(blockData.getBlock().getDurability(), fluid.i()));
        ///fluid.i() returns a constant value (i.e 1.0, 0, etc)
        //Probably like drag or something
    }
}
