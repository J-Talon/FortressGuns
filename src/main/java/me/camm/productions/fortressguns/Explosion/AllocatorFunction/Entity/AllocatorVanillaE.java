package me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosion.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class AllocatorVanillaE extends Allocator<List<Tuple2<Float,Entity>>, Tuple2<Float, Entity>> {


    public AllocatorVanillaE(World world, Vector position) {
        super(world, position);
    }


    /**
    input:  Float: radius of the explosion,  Entity: blacklist to not observe in calculations
    return: List<Tuple2<Float, Entity>> affected entities and their exposure
     */
    @Override
    public List<Tuple2<Float, Entity>> allocate(Tuple2<Float, Entity> input) {


        double x,y,z;
        x = position.getX();
        y = position.getY();
        z = position.getZ();

        float radius = input.getA();
        Entity blacklist = input.getB();

        float explosionDiameter = radius * 2.0F;
        int minX = (int)(Math.floor(x - (double)explosionDiameter - 1.0));
        int maxX = (int)(Math.floor(x + (double)explosionDiameter + 1.0));

        int minY = (int)(Math.floor(y - (double)explosionDiameter - 1.0));
        int maxY = (int)(Math.floor(y + (double)explosionDiameter + 1.0));

        int minZ = (int)(Math.floor(z - (double)explosionDiameter - 1.0));
        int maxZ = (int)(Math.floor(z + (double)explosionDiameter + 1.0));

        List<Tuple2<Float, Entity>> result = new ArrayList<>();
        BoundingBox box = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        ///min x,y,z max x,y,z
        Collection<Entity> entities = world.getNearbyEntities(box, null);

        for (Entity e: entities) {

            if (blacklist != null && (e.getUniqueId().equals(blacklist.getUniqueId())))
                continue;

            float exposure = getExposure(e,x,y,z);
            result.add(new Tuple2<>(exposure, e));
          //  System.out.println("exposure:"+exposure);
        }
        return result;

    }


}
