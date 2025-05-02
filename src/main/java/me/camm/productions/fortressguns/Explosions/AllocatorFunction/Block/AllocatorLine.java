package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AllocatorLine extends Allocator<Tuple2<Boolean, List<Block>>, Tuple2<Vector, Float>> {


    public AllocatorLine(World bukkit, Vector position) {
        super(bukkit, position);
    }

    @Override
    public Tuple2<Boolean, List<Block>> allocate(Tuple2<Vector, Float> tup) {
        Vector direction = tup.getA();
        float penetration = tup.getB();

        List<Block> positions = new ArrayList<>();

        Block current = world.getBlockAt((int)position.getX(), (int)position.getY(), (int)position.getZ());
       return null;
    }


}
