package me.camm.productions.fortressguns.Explosion.Abstract;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import static me.camm.productions.fortressguns.Util.Math.MathFG.lerp;


public abstract class Allocator<R, I> {
    //return: R
    //input: I


    protected double resolution;
    protected World world;
    protected Vector position;

    public Allocator(World bukkit, Vector position, double resolution) {
        this.resolution = resolution;
        this.world = bukkit;
        this.position = position;
    }

    public Allocator(World bukkit, Vector position) {
        this(bukkit, position, 0.3d);
    }


    public World getWorld() {
        return world;
    }

    public Vector getPosition() {
        return position;
    }


    public abstract R allocate(I inputContext);


    //x,y,z = start
    public float getExposure(Entity entity, double x, double y, double z) {

        Location start = new Location(world,x,y,z);
        Vector vecStart = start.toVector();
        BoundingBox box = entity.getBoundingBox();

        /*
        n2 - n1 is the length of a bounding box side
        d = max x  a = min x
        e = max y  b = min y
        f = max z  c = min z

         */
        double ratioX = 1.0 / ((box.getMaxX() - box.getMinX()) * 2.0 + 1.0);
        double ratioY = 1.0 / ((box.getMaxY() - box.getMinY()) * 2.0 + 1.0);
        double ratioZ = 1.0 / ((box.getMaxZ() - box.getMinZ()) * 2.0 + 1.0);

        double offsetX = (1.0 - Math.floor(1.0 / ratioX) * ratioX) / 2.0;
        double offsetZ = (1.0 - Math.floor(1.0 / ratioZ) * ratioZ) / 2.0;

        if (ratioX >= 0.0 && ratioY >= 0.0 && ratioZ >= 0.0) {
            int hits = 0;
            int total = 0;

            ///okay I think what they're doing here is a series of raytraces
            //so basically doing scanning and determining the ratio of what
            //hits or not
            for(float xMin = 0F; xMin <= 1F; xMin = (float)((double)xMin + ratioX)) {
                for(float yMin = 0F; yMin <= 1F; yMin = (float)((double)yMin + ratioY)) {
                    for(float zMin = 0F; zMin <= 1F; zMin = (float)((double)zMin + ratioZ)) {
                        ++total;
                        //d = Math.lerp()
                        double initialX = lerp(xMin, box.getMinX(), box.getMaxX());
                        double initialY = lerp(yMin, box.getMinY(), box.getMaxY());
                        double initialZ = lerp(zMin, box.getMinZ(), box.getMaxZ());

                        Vector direction = new Vector(initialX + offsetX, initialY, initialZ + offsetZ);

                        if (performTrace(vecStart, direction,entity))
                            hits ++;
                    }
                }
            }
            return (float)hits / (float)total;
        } else {
            return 0f;
        }
    }



    public boolean performTrace(Vector start, Vector direction, Entity e) {

        BoundingBox box = e.getBoundingBox();
        double distance = box.getCenter().distance(start);
        Location startLoc = start.toLocation(e.getWorld());


        RayTraceResult result = world.rayTrace(startLoc, direction, distance, FluidCollisionMode.NEVER,true,0.1,null);
        if (result == null) return false;
        Block block = result.getHitBlock();
        if (block != null) return false;

        Entity hit = result.getHitEntity();
        if (hit == null) return false;

        return hit.getUniqueId().equals(e.getUniqueId());
//
//        Vec3D startVec = new Vec3D(start.getX(), start.getY(), start.getZ());
//        Vec3D directionVec = new Vec3D(direction.getX(), direction.getY(), direction.getZ());
//
//        net.minecraft.world.level.World worldNMS = ((CraftWorld)world).getHandle();
//        net.minecraft.world.entity.Entity entityNMS = ((CraftEntity)e).getHandle();
//        MovingObjectPosition pos = worldNMS.rayTrace(new RayTrace(startVec, directionVec, RayTrace.BlockCollisionOption.a, RayTrace.FluidCollisionOption.a, entityNMS));
//        return pos.getType() == MovingObjectPosition.EnumMovingObjectType.a;
    }

}
