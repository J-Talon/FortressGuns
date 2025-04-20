package me.camm.productions.fortressguns.Explosions;

import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block.AllocatorHalfSphereB;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity.AllocatorConeE;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
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
            Vec3D lookDir = source.getLookDirection();
            this.direction = new Vector(lookDir.getX(), lookDir.getY(), lookDir.getZ());
        }
        else
            this.direction = direction.clone().normalize();
    }

    @Override
    public void perform() {

        final double resolution = 0.3;
        final double falloff = 0.2;

        Vec3D position = new Vec3D(x,y,z);
        //
        System.out.println(position);

        BlockPosition blockPos = new BlockPosition(position);
        IBlockData data = world.getType(blockPos);

        double incX = direction.getX() * resolution;
        double incY = direction.getY() * resolution;
        double incZ = direction.getZ() * resolution;

        boolean hasPenned = false;

        Material mat = Material.AIR;

        int iters = 0;
        while (!(data.isAir() && hasPenned) && penPower > 0) {

          //  System.out.println(""+hasPenned+"||"+data.isAir() +"||"+penPower);

            iters ++;
            BlockData bukkitData = CraftBlockData.fromData(data);
            Material next = bukkitData.getMaterial();

            Vec3D nextPos = position.add(incX, incY, incZ);
            BlockPosition nextBlockPos = new BlockPosition(nextPos);

            position = nextPos;

            if (nextBlockPos.getX() == blockPos.getX() &&
            nextBlockPos.getY() == blockPos.getY() &&
            nextBlockPos.getZ() == blockPos.getZ()) {
                penPower -= falloff;
                continue;
            }

            blockPos = nextBlockPos;
            data = world.getType(blockPos);

            double hardness = next.getHardness();
            hasPenned = true;

            if (next == mat) {
                penPower -= (2*hardness);
            }
            else penPower -= hardness;
            mat = next;
        }

//        System.out.println("performed pen of: "+iters);
//        System.out.println("pwr after pen: "+penPower);

        if (penPower <= 0)
            direction.multiply(-1);

        //pre-mutation
        AllocatorConeE cone = new AllocatorConeE(world, position);
        List<Tuple<Entity, Float>> affectedEntities = cone.allocate(new Tuple<>(radius, direction));
        //damage entities

        if (destroysBlocks) {

            AllocatorHalfSphereB halfSphere = new AllocatorHalfSphereB(world, position);
            Collection<BlockPosition> positions = halfSphere.allocate(new Tuple<>(radius, direction));
            processBlocks(positions);
            //destroy blocks

        }

        //post mutation
    }
}
