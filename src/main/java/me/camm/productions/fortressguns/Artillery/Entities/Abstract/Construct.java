package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Chunk;

import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;


import java.util.Set;
import java.util.UUID;

public abstract class Construct {

    protected final static double LARGE_BLOCK_LENGTH = 0.6;
    protected final static double SMALL_BLOCK_LENGTH = 0.4;

    private final UUID id;

    public Construct() {
        this.id = UUID.randomUUID();
    }




    private static final double RAD = 0.017;
    private static final double FULL_CIRCLE_DEG = 360;




    public abstract boolean spawn();

    public abstract void setChunkLoaded(boolean loaded);

    public abstract boolean chunkLoaded();

    public abstract Set<Chunk> getOccupiedChunks();

    public abstract Chunk getInitialChunk();

    public abstract void recalculateOccupiedChunks();

    public abstract Entity getCoreEntity();

   public void destroy(boolean drop, boolean explode) {
       ChunkLoader.removeActivePiece(this);
   }

    public abstract double getHealth();

    public abstract void setHealth(double health);

    /*
 In unloaded state, only core should remain with the PDC
 When construct is loaded from unloaded state, remove the old core.

 [1]
 if required chunks are loaded, spawn, else add a chunk ticket to handler
 which spawns when all chunks loaded.

 then add artillery core to shutdown manager.

 on chunk unload/plugin shutdown
  - add PDC, unload artillery if it is loaded

  on chunk load/entity load
  - see [1]


 the load function is basically the spawn() function
  */
   public abstract void unload();

   public abstract boolean isInvalid();

   public abstract ConstructType getType();


   public UUID getUUID() {
       return id;
   }


    /*
@param currentAngle, targetAngle:
 the angles in question in the format of radians

@param offsetDiff: the amount of progress the current angle makes towards the target angle

@return: the next horizontal angle to go rotate to.

@author CAMM
*/
    public double nextHorizontalAngle(double currentAngle, double targetAngle, double offsetDiff) {

        final double targetOrig = targetAngle;
        double horAngleDiff = Math.abs(currentAngle - targetAngle);


        currentAngle = Math.toDegrees(currentAngle);
        targetAngle = Math.toDegrees(targetAngle);

        if (Math.abs(currentAngle - targetAngle) <= 1)
            return Math.toRadians(targetAngle);

        //converting the current angle from -180 -> 180 format to 0->360 format
        if (currentAngle < 0)
            currentAngle += FULL_CIRCLE_DEG;

        if (targetAngle < 0)
            targetAngle += FULL_CIRCLE_DEG;


        currentAngle = currentAngle % FULL_CIRCLE_DEG;
        targetAngle = targetAngle % FULL_CIRCLE_DEG;

        if (horAngleDiff <= offsetDiff * RAD) {
            return targetOrig;
        }

        //returns a value which determines what direction the thing should rotate.
        //https://math.stackexchange.com/questions/110080/shortest-way-to-achieve-target-angle
        double diffAngle = ((targetAngle - currentAngle + 540) % 360) - 180;

        //you twisted witch
        double direction;

        if (diffAngle > 0)
            direction =  1;
        else if (diffAngle < 0)
            direction = -1;
        else
            return targetOrig;

        double offset = Math.min(offsetDiff,Math.abs(diffAngle));

        direction *= offset;
        currentAngle += direction;

        return Math.toRadians(currentAngle);

    }


    /*
@param currentAngle, targetAngle:
 the angles in question in the format of 90 -> -90 degrees in radian measure

@param diffOffset:
  the amount progress the original angle should make towards the target

@return: shortest direction to target angle. Either 1,-1, or 0 for the
  90 ->-90 angle format in radian measure.



@author CAMM
     */
    protected double nextVerticalAngle(double currentAngle, double targetAngle, double diffOffset) {

        double diff = targetAngle - currentAngle;
        double diffMag = Math.abs(diff);

        if (diffMag <= diffOffset * RAD) {
            return targetAngle;
        }

        //return the current angle + the offset * direction
        return currentAngle + Math.toRadians((Math.abs(diff)/diff) * diffOffset);
    }





    public static Vector eulerToVec(EulerAngle aim) {
        //getting the values for the projectile velocity.
        //tan and sine are (-) since MC's grid is inverted
        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());
        return new Vector(x,y,z);

    }


    //only for when the up direction is <0,1,0>
    //whhiiiiich should be true generally here
    @Deprecated  //see StandHelper.getLookatRotation()
    public static EulerAngle vecToEuler(Vector vec) {
        Vector vec2 = vec.clone().normalize();
        double y = -Math.atan2(vec2.getX(), vec2.getZ());
        double x = -Math.asin(vec2.getY());
        return new EulerAngle(x, y, 0);
    }



}
