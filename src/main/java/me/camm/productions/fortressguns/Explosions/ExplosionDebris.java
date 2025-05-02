package me.camm.productions.fortressguns.Explosions;

import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block.AllocatorHalfSphereB;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity.AllocatorConeE;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class ExplosionDebris extends ExplosionFG {


    private final Vector direction;
    private float penPower;


    public ExplosionDebris(double x, double y, double z, World world, float radius, Entity source, boolean destructive, Vector direction, float penPower) {
        super(x, y, z, world, radius, source, destructive);
        System.out.println("Start pos: "+x+" "+y +" "+z);
        this.penPower = penPower;

        if (direction.lengthSquared() == 0) {
            this.direction = source.getLocation().getDirection();
        }
        else
            this.direction = direction.clone().normalize();
    }

    @Override
    public void perform() {

        final double resolution = 0.3;
        final double falloff = 0.2;

        Vector position = new Vector(x,y,z);
        //
        System.out.println(position);

        Block block = world.getBlockAt((int)x, (int)y, (int)z);

        Material blockMat = block.getType();

        double incX = direction.getX() * resolution;
        double incY = direction.getY() * resolution;
        double incZ = direction.getZ() * resolution;

        boolean hasPenned = false;

        Material mat = Material.AIR;

        while (!(blockMat.isAir() && hasPenned) && penPower > 0) {

          //  System.out.println(""+hasPenned+"||"+data.isAir() +"||"+penPower);

            position.add(new Vector(incX, incY, incZ));
            Block nextBlock = world.getBlockAt((int)x, (int)y, (int)z);

            if (nextBlock.getLocation().distanceSquared(block.getLocation()) == 0) {
                penPower -= falloff;
                continue;
            }

            block = nextBlock;
            blockMat = nextBlock.getType();

            double hardness = block.getType().getHardness();
            hasPenned = true;

            if (blockMat == mat) {
                penPower -= (2*hardness);
            }
            else penPower -= hardness;
            mat = blockMat;
        }

        if (penPower <= 0)
            direction.multiply(-1);

        //pre-mutation
        AllocatorConeE cone = new AllocatorConeE(world, position);
        List<Tuple2<Entity, Float>> affectedEntities = cone.allocate(new Tuple2<>(radius, direction));
        //damage entities

        if (destroysBlocks) {
            AllocatorHalfSphereB halfSphere = new AllocatorHalfSphereB(world, position);
            Collection<Block> positions = halfSphere.allocate(new Tuple2<>(radius, direction));
            processDrops(positions);
            //destroy blocks

        }

        //post mutation
    }
}
