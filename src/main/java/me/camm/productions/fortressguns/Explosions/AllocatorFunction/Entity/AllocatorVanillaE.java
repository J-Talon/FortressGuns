package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

import java.util.Collection;
import java.util.List;

public class AllocatorVanillaE extends Allocator<List<Entity>, Tuple<Float, Entity>> {


    public AllocatorVanillaE(World nmsWorld, Vec3D position) {
        super(nmsWorld, position);
    }


    /*
    input:  float - radius,  entity: blacklist
    return: List<entity> affected entities
     */
    @Override
    public List<Entity> allocate(Tuple<Float, Entity> input) {


        double x,y,z;
        x = position.getX();
        y = position.getY();
        z = position.getZ();

        float radius = input.a();
        Entity blacklist = input.b();

        float explosionDiameter = radius * 2.0F;
        int minX = MathHelper.floor(x - (double)explosionDiameter - 1.0);
        int maxX = MathHelper.floor(x + (double)explosionDiameter + 1.0);

        int minY = MathHelper.floor(y - (double)explosionDiameter - 1.0);
        int maxY = MathHelper.floor(y + (double)explosionDiameter + 1.0);

        int minZ = MathHelper.floor(z - (double)explosionDiameter - 1.0);
        int maxZ = MathHelper.floor(z + (double)explosionDiameter + 1.0);

        ///min x,y,z max x,y,z
        return nmsWorld.getEntities(blacklist, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

    }
}
