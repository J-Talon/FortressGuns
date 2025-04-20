package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.util.Vector;

import java.util.List;

public class AllocatorConeE extends Allocator<List<Tuple<Entity, Float>>,Tuple<Float, Vector>> {

    public AllocatorConeE(World nmsWorld, Vec3D position) {
        super(nmsWorld, position);
    }

    @Override
    public List<Tuple<Entity,Float>> allocate(Tuple<Float, Vector> inputContext) {
        return null;
    }
}
