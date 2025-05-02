package me.camm.productions.fortressguns.Explosion;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block.AllocatorHalfSphereB;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorConeE;
import me.camm.productions.fortressguns.Util.Tuple2;
import me.camm.productions.fortressguns.Util.Tuple3;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class ExplosionDebris extends ExplosionFG {


    private final Vector direction;


    public ExplosionDebris(double x, double y, double z, World world, float radius, Entity source, boolean destructive, Vector direction) {
        super(x, y, z, world, radius, source, destructive);
        System.out.println("Start pos: "+x+" "+y +" "+z);
        if (direction.lengthSquared() == 0) {
            this.direction = source.getLocation().getDirection();
        }
        else
            this.direction = direction.clone().normalize();
    }

    @Override
    public void perform() {

        ExplosionEffect effect;
        Vector position = new Vector(x,y,z);
        final float SUBTRACTION = 0.2f;  //put in config
        //pre-mutation
        AllocatorConeE cone = new AllocatorConeE(world, position, SUBTRACTION);
        List<Tuple2<Entity, Float>> affectedEntities = cone.allocate(new Tuple3<>(radius, direction, source));
        //damage entities

        for (Tuple2<Entity, Float> tup: affectedEntities) {
            damageEntity(tup.getA(), tup.getB());
        }

        if (destroysBlocks) {
            AllocatorHalfSphereB halfSphere = new AllocatorHalfSphereB(world, position);
            Collection<Block> positions = halfSphere.allocate(new Tuple2<>(radius, direction));
            processDrops(positions);
            //destroy blocks
        }

        //post mutation
    }
}
