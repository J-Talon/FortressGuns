package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

public class AllocatorConeE extends Allocator<List<Tuple2<Entity, Float>>,Tuple2<Float, Vector>> {

    public AllocatorConeE(World world, Vector position) {
        super(world, position);
    }

    @Override
    public List<Tuple2<Entity,Float>> allocate(Tuple2<Float, Vector> inputContext) {
        return null;
    }
}
