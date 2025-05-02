package me.camm.productions.fortressguns.Artillery.Entities;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import net.minecraft.core.Vector3f;
import net.minecraft.server.level.WorldServer;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class StandHelper
{

    ///this is crude collision detection; it only works most of the time
    //cause the armorstand hitbox is a box, not a point
    //we may want to refine this.
    public static boolean isPosClear(Location loc) {
        return loc.getBlock().isPassable();
    }


    public static boolean isPosObstructedRaw(Location rawLoc) {

        /*
        0  --> 1.975
        1.725 is base of head

        + 0.25 in x, z
        - 0.25 in x, z

         */
        double heightLow = 1.725;
        double heightHigh = 1.975;
        double horOffset = 0.25;

        Vector[] offsets = new Vector[]{
                new Vector(horOffset, heightLow, horOffset),
                new Vector(-horOffset, heightLow, horOffset),
                new Vector(horOffset, heightLow, -horOffset),
                new Vector(-horOffset, heightLow, -horOffset),

                new Vector(horOffset, heightHigh, horOffset),
                new Vector(-horOffset, heightHigh, horOffset),
                new Vector(horOffset, heightHigh, -horOffset),
                new Vector(-horOffset, heightHigh, -horOffset)
        };

        boolean clear = true;
        for (Vector offset: offsets) {
            Location current = new Location(rawLoc.getWorld(), rawLoc.getX(), rawLoc.getY(),rawLoc.getZ());
            current.add(offset);
            clear = clear && isPosClear(current);
        }

        return !clear;
    }




    //Returns the player's horizontal rotation in a language that armorstands understand
    //and will happily and accurately flipping orient themselves to. (Feel the frustration!)
    //@author CAMM
    public static EulerAngle getASFace(EntityHuman human) {
        //x,y,z
        //x -> vertical rotation
        //y -> horizontal rotation
        return new EulerAngle(Math.toRadians(human.getXRot()), Math.toRadians(human.getHeadRotation() + 180),0);
    }

    //Returns a rotation in a language that armorstands understand and in which they can orient themselves to.
    //@author CAMM
    //@param horizontal, vertical: angles in the -180 -> 180 format and -90 -> 90 format in degrees
    public static EulerAngle getStandFacing(double horizontal, double vertical) {
        //x,y,z
        //x -> vertical rotation
        //y -> horizontal rotation
        return new EulerAngle(Math.toRadians(vertical), Math.toRadians(horizontal) + 180,0);
    }









    public static void setHead(ItemStack stack, EntityArmorStand stand){
        stand.setSlot(EnumItemSlot.f, CraftItemStack.asNMSCopy(stack));
    }

    public static void teleport(EntityArmorStand stand, Location loc){
        teleport(stand, loc.getX(), loc.getY(),loc.getZ());
    }



    public static void teleport(EntityArmorStand stand, double x, double y, double z) {
        stand.teleportAndSync(x,y,z);
        stand.g(x,y,z);
    }

    public static void setRotation(EntityArmorStand stand, float x, float y){
        stand.setHeadPose(new Vector3f((float)Math.toDegrees(x),(float)Math.toDegrees(y),0));
    }

    public static void setRotation(EntityArmorStand stand, EulerAngle angle) {
        setRotation(stand, (float)angle.getX(),(float)angle.getY());
    }

    //does not spawn into the world - you need to do that.
    public static ArtilleryPart createInvisiblePart(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery){
     ArtilleryPart part = createVisiblePart(loc, head, rotation, world, artillery);
     if (part == null)
         return null;

      return setInvisible(part);
    }

    ////does not spawn into the world - you need to do that.
    public static FireTrigger createTrigger(Location loc, World world, Artillery body) {

        if (isPosObstructedRaw(loc))
            return null;

        WorldServer nms = ((CraftWorld)world).getHandle();
        FireTrigger trigger = new FireTrigger(nms, body, loc);
        trigger.teleportAndSync(loc.getX(), loc.getY(), loc.getZ());
       // nms.addEntity(trigger);

        trigger.setNoGravity(true);
        trigger.setInvisible(true);
        return trigger;

    }


    ////does not spawn into the world - you need to do that.
    public static ArtilleryPart createVisiblePart(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryPart part = new ArtilleryPart(nms, artillery, loc);
        part.teleportAndSync(loc.getX(),loc.getY(),loc.getZ());

        ///if the position is in a block you should return null
        if (isPosObstructedRaw(loc))
            return null;

        if (rotation != null)
         setRotation(part, rotation);

        if (head != null)
            setHead(head, part);

        part.setNoGravity(true);

        //true means no base plate
        part.setBasePlate(true);


       // nms.addEntity(part);
        return part;
    }

    private static ArtilleryPart setInvisible(ArtilleryPart part) {
        part.setInvisible(true);
        return part;
    }


    ////does not spawn into the world - you need to do that.
    public static ArtilleryCore createCore(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery) {

        //by implementation the core should not be obstructed when spawning.
        //this should only occur if we're loading from file (cause you need to click the air to spawn artys)

        //one possibility is if there is a fallingblock -i.e sand.
        // ^ account for this

        if (isPosObstructedRaw(loc))
            return null;

        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryCore part = new ArtilleryCore(nms, artillery, loc.getX(),loc.getY(),loc.getZ());
        part.teleportAndSync(loc.getX(),loc.getY(),loc.getZ());
        setRotation(part, rotation);
        setHead(head, part);
        part.setNoGravity(true);
        part.setInvisible(true);
      //  nms.addEntity(part);
        return part;
    }



    /*
    Given a source and a destination, calculates the euler angle needed for an armorstand to look at that
    destination from the source.
     */
    public static EulerAngle getLookatRotation(Location source, Location lookat) {
        return getLookatRotation(lookat.clone().subtract(source).toVector());
    }


    //@author CAMM
    /*
    the feeling when you rediscover an answer to a problem that you wrote but completely forgot about
    ... this is basically vec to euler but for my specific use case *facepalm*
     */
    public static EulerAngle getLookatRotation(Vector direction) {

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




    /*
    Rotates the armorstand head such that the headpiece rotates as if the pivot point is the center of the head.
    may move the armorstand location slightly.

    Note: should we return the armorstand's original data so that we can return to the original orientation?
     */
    public static void rotateInPlace() {

    }
















}
