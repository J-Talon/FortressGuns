package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorConeE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectDebris;
import me.camm.productions.fortressguns.Util.Tuple2;
import me.camm.productions.fortressguns.Util.Tuple3;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ExplosionDebris extends ExplosionFG {


    private final Vector direction;
    private final Block context;


    public ExplosionDebris(double x, double y, double z, World world, float radius, Entity source, boolean destructive, Vector direction, @Nullable Block context) {
        super(x, y, z, world, radius, source, destructive);
        this.context = context;

        if (direction.lengthSquared() == 0) {
            this.direction = source.getLocation().getDirection();
        }
        else
            this.direction = direction.clone().normalize();
    }

    @Override
    public void perform() {


        Vector position = new Vector(x,y,z);
        final float ANGLE_SUBTRACTION = 0f;  //put in config
        //pre-mutation
        AllocatorConeE cone = new AllocatorConeE(world, position, ANGLE_SUBTRACTION);
        List<Tuple2<Entity, Float>> affectedEntities = cone.allocate(new Tuple3<>(radius, direction, source));
        //damage entities

        for (Tuple2<Entity, Float> tup: affectedEntities) {
            damageEntity(tup.getA(), tup.getB());
        }

        EffectDebris debris = new EffectDebris();
        debris.preMutation(this, new Tuple2<>(1.0, context));
        //post mutation
    }


    @Override
    public float getMaxDamage() {
        return 10f;
    }


    @Override
    public double getFalloff(double distanceSquared) {

        if (distanceSquared == 0)
            return getMaxDamage();

        final double THRESHOLD = 0.01;
        double scale = Math.pow(getRadius(),2) * THRESHOLD;
        double falloff = Math.min(scale * (1/distanceSquared),getMaxDamage());
        if ( falloff > THRESHOLD)
            return falloff;
        else return 0;
    }
}
