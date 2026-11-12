package me.camm.productions.fortressguns.Util.Math.RayPosition;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

//@author CAMM
public class Boxer
{
    private final double addX, multiplierX;
    private final double addY, multiplierY;
    private final double addZ, multiplierZ;

    /**
     *
     * @param dir direction of the projectile
     * @param origin origin of the projectile
     */
    public Boxer(@NotNull Vector dir, @NotNull Vector origin){

        addX = origin.getX();
        addY = origin.getY();
        addZ = origin.getZ();

        multiplierX = dir.getX();
        multiplierY = dir.getY();
        multiplierZ = dir.getZ();
    }

    /**
     *
     * @param coordinateX coordinate of the target
     * @return value of x for the equation of the 3d line for the projectile to hit, 0 if invalid
     */
    public double getXFactor(double coordinateX){
        try {
            return (coordinateX - addX) / multiplierX;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public double getZFactor(double coordinateZ){
        try {
            return (coordinateZ - addZ) / multiplierZ;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public double getYFactor(double coordinateY){
        try {
            return (coordinateY - addY) / multiplierY;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     *
     * @param yCoord coordinate to do calculations off of
     * @return vector of the coordinate of hit
     */
    public Vector getYPoint(double yCoord){
        double factor = getYFactor(yCoord);
        double x = addX+multiplierX*factor;
        double y = addY+multiplierY*factor;
        double z = addZ+multiplierZ*factor;
        return new Vector(x,y,z);
    }

    public Vector getZPoint(double zCoord){
        double factor = getZFactor(zCoord);
        double x = addX+multiplierX*factor;
        double y = addY+multiplierY*factor;
        double z = addZ+multiplierZ*factor;
        return new Vector(x,y,z);
    }

    public Vector getXPoint(double xCoord){
        double factor = getXFactor(xCoord);

        double x = addX + multiplierX*factor;
        double y = addY + multiplierY*factor;
        double z = addZ + multiplierZ*factor;

        return new Vector(x,y,z);
    }
}