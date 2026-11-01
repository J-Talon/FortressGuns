package me.camm.productions.fortressguns.Artillery.Entities.Generation;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Property.IRideable;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;


public class ConstructUtils {


    //in the future, if we plan to make this version compatible with multiple versions,
    //this will help


    public static @Nullable Component getComponentRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (nms instanceof Component) return (Component)nms;
        return null;
    }


    public static boolean isComponentOf(Construct construct, Entity entity) {
        Component comp = getComponentRef(entity);
        if (comp == null) return false;

        return comp.getBody().equals(construct);
    }



    public static @Nullable IRideable getRideableRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (!(nms instanceof Component comp)) return null;

        Construct body = comp.getBody();
        if (body instanceof IRideable ride) return ride;
        return null;
    }

    public static boolean isOperatorOf(Player player, Construct struct) {

        if (player == null) return false;

        if (!(struct instanceof IRideable ride)) return false;
        Player rider = ride.getRider();
        if (rider == null) return false;

        return player.getUniqueId().equals(rider.getUniqueId());

    }


    public static boolean isProjectileFG(Entity e) {
        return ( ((CraftEntity)e).getHandle() instanceof ProjectileFG);
    }


    public static void openMenu(Artillery body, Player player, ItemStack stack) {

        ConstructInventory menu;
        InventoryGroup group = body.getInventoryGroup();
        FIND_INV:
        {

            if (body instanceof RapidFire rapid && rapid.isJammed()) {
                menu = group.getInventoryByCategory(InventoryCategory.JAM_CLEAR);
                break FIND_INV;
            }

            if (ItemUtils.isAmmoItem(stack) != null) {
                menu = group.getInventoryByCategory(InventoryCategory.RELOADING);
            } else {
                menu = group.getInventoryByCategory(InventoryCategory.MENU);
            }
        }

        if (menu == null) {
            FortressGuns.getInstance().getLogger().warning("Inventory instance returned null!");
            return;
        }
        group.openInventory(menu, player);
    }




    public static boolean isInBlock(net.minecraft.world.entity.Entity nms) {
        Vec3D check = new Vec3D(nms.locX(), nms.getHeadY(), nms.locZ());

        World world = nms.t;

        float entityWidth = nms.getWidth() * 0.8F;

                /*
        n2 - n1 is the length of a bounding box side
        d = max x  a = min x
        e = max y  b = min y
        f = max z  c = min z

         */

        AxisAlignedBB axisalignedbb = AxisAlignedBB.a(check,entityWidth, 1.0E-6, entityWidth);
        //bb() returns Vec3D(this.locX(), this.getHeadY(), this.locZ());

        return world.b(nms, axisalignedbb, (iblockdata, blockposition) -> {
            return iblockdata.o(world, blockposition);
        }).findAny().isPresent();
    }



    public static boolean isInBlock(ArmorStand entity, Vector nextPosition) {
        double width = entity.getWidth() * 0.8F;
        double height = entity.getHeight();

        Vector check = new Vector(nextPosition.getX(), nextPosition.getY() + height, nextPosition.getZ());
        BoundingBox box = boxFromCenterPoint(check, width, 1.0E-6, width);

        List<Block> blocks = MathFG.getRoughCollisions(entity.getEyeLocation(), entity.getWorld());
        return !MathFG.overlaps(box, blocks).isEmpty();
    }


    public static boolean isInBlock(net.minecraft.world.entity.Entity nms, Vector nextPosition) {
        float entityWidth = nms.getWidth() * 0.8F;
        float entityHeight = nms.getHeadHeight();
        World world = nms.t;

        Vec3D check = new Vec3D(nextPosition.getX(), nextPosition.getY() + (double)entityHeight, nextPosition.getZ());

        AxisAlignedBB boundingBox = AxisAlignedBB.a(check, entityWidth, 1.0E-6, entityWidth);

        return world.b(nms, boundingBox, (blockType, position) ->
                blockType.o(world, position)).findAny().isPresent();
    }



    public static BoundingBox boxFromCenterPoint(Vector location, double spanX, double spanY, double spanZ) {
        return new BoundingBox(
                location.getX() - spanX / (double)2.0F,
                location.getY() - spanY / (double)2.0F,
                location.getZ() - spanZ / (double)2.0F,

                location.getX() + spanX / (double)2.0F,
                location.getY() + spanY / (double)2.0F,
                location.getZ() + spanZ / (double)2.0F
        );
    }



    public static List<Block> getHeadBlocks(ArmorStand entity) {

        double width = entity.getWidth() * 0.8F;

        Vector check = entity.getEyeLocation().toVector();
        BoundingBox box = boxFromCenterPoint(check, width, 1.0E-6, width);

        List<Block> blocks = MathFG.getRoughCollisions(entity.getEyeLocation(), entity.getWorld());
        return MathFG.overlaps(box, blocks);
    }
}
