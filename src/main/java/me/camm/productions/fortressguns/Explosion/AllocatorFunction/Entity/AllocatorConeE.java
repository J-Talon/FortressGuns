package me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosion.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import me.camm.productions.fortressguns.Util.Tuple3;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class AllocatorConeE extends Allocator<List<Tuple2<Entity, Float>>,Tuple3<Float, Vector, Entity>> {

    private final float subtraction;
    public AllocatorConeE(World world, Vector position, float sub) {
        super(world, position);
        subtraction = sub;
    }

    //allocates in a 1/2 circle dependant on an angle
    //input: radius, direction, blacklist
    //output: hit entity, exposure (including blocks) from [0-1]
    @Override
    public List<Tuple2<Entity,Float>> allocate(Tuple3<Float, Vector, Entity> inputContext) {

        float radius = inputContext.getA();

        double x,y,z;
        x = position.getX();
        y = position.getY();
        z = position.getZ();

        //min xyz, max xyz
        BoundingBox box = new BoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);

        class PredicateBox implements Predicate<Entity> {
            private final Entity blacklist;
            public PredicateBox(Entity blacklist) {
                this.blacklist = blacklist;
            }
            @Override
            public boolean test(Entity entity) {
                return !(entity.equals(blacklist));
            }
        }

        Vector direction = inputContext.getB();
        Collection<Entity> entities = world.getNearbyEntities(box, new PredicateBox(inputContext.getC()));
        List<Tuple2<Entity, Float>> exposures = new ArrayList<>();

        for (Entity entity: entities) {
            double exposure = getExposure(entity, x,y,z);
            Vector toEntity = entity.getLocation().toVector().subtract(position);
            double angle = Math.max(0, toEntity.dot(direction) - subtraction);
            exposures.add(new Tuple2<>(entity, (float)(exposure * angle)));
        }

        return exposures;
    }
}
