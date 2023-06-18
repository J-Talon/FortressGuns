package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import org.bukkit.Chunk;


import java.util.Set;

public abstract class Construct {
    public abstract void spawn();

    public abstract void setChunkLoaded(boolean loaded);

    public abstract Set<Chunk> getLoaders();

   public abstract void unload(boolean drop, boolean explode);
   public abstract boolean isInvalid();


    /*
@param currentAngle, targetAngle:
 the angles in question in the format of radians

@return: the next horizontal angle to go rotate to.

@author CAMM
*/
    public double nextHorizontalAngle(double currentAngle, double targetAngle) {

        currentAngle = Math.toDegrees(currentAngle);
        targetAngle = Math.toDegrees(targetAngle);

        if (Math.abs(currentAngle - targetAngle) <= 1)
            return Math.toRadians(targetAngle);

        //converting the current angle from -180 -> 180 format to 0->360 format
        if (currentAngle < 0)
            currentAngle += 360;

        if (targetAngle < 0)
            targetAngle += 360;

        double diffAngle = ((targetAngle - currentAngle + 540) % 360) - 180;
        int dir;

        if (diffAngle > 0)
            dir =  1;
        else if (diffAngle < 0)
            dir = -1;
        else dir = 0;

        double offset = Math.min(1,Math.abs(diffAngle));
        dir *= offset;
        currentAngle += dir;


        return Math.toRadians(currentAngle);

    }


    /*
@param currentAngle, targetAngle:
 the angles in question in the format of 90 -> -90 degrees in radian measure

@return: shortest direction to target angle. Either 1,-1, or 0 for the
  90 ->-90 angle format in radian measure.

@author CAMM
     */
    protected double nextVerticalAngle(double currentAngle, double targetAngle) {
        double diff = targetAngle - currentAngle;
        return diff == 0 ? targetAngle : currentAngle + Math.toRadians((Math.abs(diff)/diff));
    }
}
