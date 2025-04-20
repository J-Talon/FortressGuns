package me.camm.productions.fortressguns.Explosions.Abstract;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class Allocator<T, T1> {


    protected double resolution;
    protected World nmsWorld;
    protected Vec3D position;

    public Allocator(World nmsWorld, Vec3D position, double resolution) {
        this.resolution = resolution;
        this.nmsWorld = nmsWorld;
        this.position = position;
    }

    public Allocator(World nmsWorld, Vec3D position) {
        this(nmsWorld, position, 0.3d);
    }

    protected org.bukkit.World getBukkitWorld() {
        return nmsWorld.getWorld();
    }

    protected Location getBukkitOrigin() {
     return new Location(getBukkitWorld(), position.getX(), position.getY(), position.getZ());
    }

    public World getNmsWorld() {
        return nmsWorld;
    }

    public Vec3D getPosition() {
        return position;
    }


    public abstract T allocate(T1 inputContext);

    public Optional<Float> getBlockResistance(IBlockData blockData, Fluid fluid) {
        return blockData.isAir() && fluid.isEmpty() ?
                Optional.empty() :
                Optional.of(Math.max(blockData.getBlock().getDurability(), fluid.i()));
        ///fluid.i() returns a constant value (i.e 1.0, 0, etc)
        //Probably like drag or something
    }
}
