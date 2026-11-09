package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
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
import org.jetbrains.annotations.Nullable;

public class StandHelper
{


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
      return setInvisible(part);
    }

    ////does not spawn into the world - you need to do that.
    public static Component createTrigger(Location loc, World world, Artillery body) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryPart trigger = new ArtilleryPart(nms, body, loc);
        trigger.teleportAndSync(loc.getX(), loc.getY(), loc.getZ());

        trigger.setNoGravity(true);
        trigger.setInvisible(true);
        return trigger;

    }


    ////does not spawn into the world - you need to do that.
    public static ArtilleryPart createVisiblePart(Location loc, ItemStack head, @Nullable EulerAngle rotation, World world, Artillery artillery) {
        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryPart part = new ArtilleryPart(nms, artillery, loc);
        part.teleport(loc.getX(), loc.getY(), loc.getZ());

        if (rotation != null)
            part.setRotation(rotation);

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
    public static Component createCore(Location loc, ItemStack head, EulerAngle rotation, World world, Artillery artillery) {

        //by implementation the core should not be obstructed when spawning.
        //this should only occur if we're loading from file (cause you need to click the air to spawn artys)

        //one possibility is if there is a fallingblock -i.e sand.
        // ^ account for this

        WorldServer nms = ((CraftWorld)world).getHandle();
        ArtilleryPart part = new ArtilleryPart(nms, artillery, loc.getX(),loc.getY(),loc.getZ());
        part.teleportAndSync(loc.getX(),loc.getY(),loc.getZ());

        setRotation(part, rotation);
        setHead(head, part);
        part.setNoGravity(true);
        part.setInvisible(true);
      //  nms.addEntity(part);
        return part;
    }
}
