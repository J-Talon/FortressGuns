package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.AxisAlignedBB;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;

public class AllocatorVanillaE extends Allocator<List<Entity>, Tuple2<Float, Entity>> {


    public AllocatorVanillaE(World world, Vector position) {
        super(world, position);
    }


    /*
    input:  float - radius,  entity: blacklist
    return: List<entity> affected entities
     */
    @Override
    public List<Entity> allocate(Tuple2<Float, Entity> input) {


        double x,y,z;
        x = position.getX();
        y = position.getY();
        z = position.getZ();

        float radius = input.getA();
        Entity blacklist = input.getB();

        float explosionDiameter = radius * 2.0F;
        int minX = MathHelper.floor(x - (double)explosionDiameter - 1.0);
        int maxX = MathHelper.floor(x + (double)explosionDiameter + 1.0);

        int minY = MathHelper.floor(y - (double)explosionDiameter - 1.0);
        int maxY = MathHelper.floor(y + (double)explosionDiameter + 1.0);

        int minZ = MathHelper.floor(z - (double)explosionDiameter - 1.0);
        int maxZ = MathHelper.floor(z + (double)explosionDiameter + 1.0);

        class BoxFilter implements Predicate<Entity> {
            private final Entity blacklist;
            public BoxFilter(Entity blacklist) {
                this.blacklist = blacklist;
            }

            @Override
            public boolean test(Entity entity) {
                return !entity.equals(blacklist);
            }
        }

        ///min x,y,z max x,y,z
        return (List<Entity>) world.getNearbyEntities(new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ), new BoxFilter(blacklist));

    }


}
