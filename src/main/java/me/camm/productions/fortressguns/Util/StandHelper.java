package me.camm.productions.fortressguns.Util;

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

public class StandHelper
{


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

    public static ArtilleryPart spawnPart(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery){
      return setInvisible(spawnVisiblePart(loc, head, rotation, world, artillery));
    }

    public static FireTrigger spawnTrigger(Location loc, World world, Artillery body) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        FireTrigger trigger = new FireTrigger(nms, body, loc);
        trigger.teleportAndSync(loc.getX(), loc.getY(), loc.getZ());
        nms.addEntity(trigger);



        trigger.setNoGravity(true);
        trigger.setInvisible(true);
        return trigger;

    }


    public static ArtilleryPart spawnVisiblePart(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryPart part = new ArtilleryPart(nms, artillery, loc);
        part.teleportAndSync(loc.getX(),loc.getY(),loc.getZ());

        if (rotation != null)
         setRotation(part, rotation);

        if (head != null)
            setHead(head, part);

        part.setNoGravity(true);

        //true means no base plate
        part.setBasePlate(true);


        nms.addEntity(part);
        return part;
    }

    private static ArtilleryPart setInvisible(ArtilleryPart part) {
        part.setInvisible(true);
        return part;
    }

    public static ArtilleryCore getCore(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryCore part = new ArtilleryCore(nms, artillery, loc.getX(),loc.getY(),loc.getZ());
        part.teleportAndSync(loc.getX(),loc.getY(),loc.getZ());
        setRotation(part, rotation);
        setHead(head, part);
        part.setNoGravity(true);
        part.setInvisible(true);
        nms.addEntity(part);
        return part;
    }




}
