package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import org.bukkit.Chunk;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;


import java.util.Set;

public abstract class Construct {

    protected final static double LARGE_BLOCK_LENGTH = 0.6;
    protected final static double SMALL_BLOCK_LENGTH = 0.4;




    private static final double RAD = 0.017;
    private static final double FULL_CIRCLE_DEG = 360;

    public abstract boolean spawn();

    public abstract void setChunkLoaded(boolean loaded);

    public abstract Set<Chunk> getOccupiedChunks();

   public abstract void unload(boolean drop, boolean explode);
   public abstract boolean isInvalid();





    /*
@param currentAngle, targetAngle:
 the angles in question in the format of radians

@param offsetDiff: the amount of progress the current angle makes towards the target angle

@return: the next horizontal angle to go rotate to.

@author CAMM
*/
    public double nextHorizontalAngle(double currentAngle, double targetAngle, double offsetDiff) {

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
            return Math.toRadians(targetAngle);
        }

        //returns a value which determines what direction the thing should rotate.
        //https://math.stackexchange.com/questions/110080/shortest-way-to-achieve-target-angle
        double diffAngle = ((targetAngle - currentAngle + 540) % 360) - 180;

        int direction;

        if (diffAngle > 0)
            direction =  1;
        else if (diffAngle < 0)
            direction = -1;
        else
            direction = 0;

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
}
