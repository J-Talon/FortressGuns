package me.camm.productions.fortressguns.Util.Math;

import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;

public class MathFG {

    static Random rand = new Random();
    public static final double RAD = 0.017;
    public static final double FULL_CIRCLE_DEG = 360;



    public static double linearInterpolate(double increment, double start, double end) {
        return start + increment * (end - start);
    }


    public static double randomDouble() {
        return rand.nextDouble();
    }



    /*
    Returns a random vector which is orthagonal to the current vector, or a
    0 vector if not possible
     */
    public static Vector randomOrthagonal(Vector other) {
        double denom = Double.NaN;
        double linComb = Double.NaN;  ///linear combination method to get orthagonal
        double mult1, mult2;
        mult1 = rand.nextDouble() - 0.5;
        mult2 = rand.nextDouble() - 0.5;

        //A is orthagonal to B if A dot B = 0
        /*
        A = {a1,a2,a3}
        B = {b1, b2, b3}
        A ' B means:

        a1 * b1 + a2 * b2 + a3 * b3 = 0
        where mult1 and mult2 serve as either b1, b2, or b3 depending on the situation

        We're basically solving for either b1, b2, or b3 depending on what a1, a2, and a3 are
         */

        if (other.getX() != 0) {
            denom = other.getX();
            linComb = other.getY() * mult1 - other.getZ() * mult2;
        }
        else if (other.getY() != 0) {
            denom = other.getY();
            linComb = -other.getX() * mult1 - other.getZ() * mult2;
        }
        else if (other.getZ() != 0) {
            denom = other.getZ();
            linComb = -other.getX() * mult1 - other.getY() * mult1;
        }

        if (Double.isNaN(denom) && Double.isNaN(linComb))
            return new Vector(0,0,0);
        else return new Vector(mult1, mult2, linComb / denom).normalize();

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
    public static double nextVerticalAngle(double currentAngle, double targetAngle, double diffOffset) {

        double diff = targetAngle - currentAngle;
        double diffMag = Math.abs(diff);

        if (diffMag <= diffOffset * RAD) {
            return targetAngle;
        }

        //return the current angle + the offset * direction
        return currentAngle + Math.toRadians((Math.abs(diff)/diff) * diffOffset);
    }



    /*
@param currentAngle, targetAngle:
the angles in question in the format of radians

@param offsetDiff: the amount of progress the current angle makes towards the target angle

@return: the next horizontal angle to go rotate to.

@author CAMM
*/
    public static double nextHorizontalAngle(double currentAngle, double targetAngle, double offsetDiff) {

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



    public static Vector eulerToVec(EulerAngle aim) {
        //getting the values for the projectile velocity.
        //tan and sine are (-) since MC's grid is inverted
        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());
        return new Vector(x,y,z);

    }



    /*
Given a source and a destination, calculates the euler angle needed for an armorstand to look at that
destination from the source.
 */
    public static EulerAngle getLookatRotation(Location source, Location lookat) {
        return vecToEulerXY(lookat.clone().subtract(source).toVector());
    }


    //@author CAMM
    /*
    the feeling when you rediscover an answer to a problem that you wrote but completely forgot about
    ... this is basically vec to euler but for my specific use case *facepalm*
     */
    public static EulerAngle vecToEulerXY(Vector direction) {

        //x,y,z should be the diff between the dest and source.
        //all you gotta know is that euler angles are basically 3 degrees mashed together
        double x = direction.getX();
        double z = direction.getZ();
        double hypotenuseHorizontal = Math.sqrt(x * x + z * z);

        double horAngle;

        //basically straight up
        if (hypotenuseHorizontal == 0) {
            horAngle = 0;
        }
        else {
            horAngle = Math.acos(z / hypotenuseHorizontal);
            if (x < 0) {
                horAngle *= -1;
            }
        }

        double y = direction.getY();
        double vertAngle;
        double hypotenuseTotal = Math.sqrt( x * x + y * y + z * z);
        if (hypotenuseTotal == 0) {
            vertAngle = 0;
        }
        else {
            vertAngle = Math.asin(y / hypotenuseTotal);
        }

        return new EulerAngle(vertAngle, horAngle, 0);
    }


    /**
     Input: x, y: non negative integers x,y
     @return hash for x and y, also non negative
     <a href="http://szudzik.com/ElegantPairing.pdf">...</a>
     */
    public static int pair2(int x, int y) {
        if (Math.max(x,y) == x)
            return y * y + x;
        return x * x + x + y;
    }


    //only for when the up direction is <0,1,0>
    //whhiiiiich should be true generally here
    @Deprecated  //see vecToEulerXY
    public static EulerAngle vecToEuler(Vector vec) {
        Vector vec2 = vec.clone().normalize();
        double y = -Math.atan2(vec2.getX(), vec2.getZ());
        double x = -Math.asin(vec2.getY());
        return new EulerAngle(x, y, 0);
    }







}
