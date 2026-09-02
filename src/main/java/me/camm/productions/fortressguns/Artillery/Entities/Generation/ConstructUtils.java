package me.camm.productions.fortressguns.Artillery.Entities.Generation;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ConstructUtils {


    //in the future, if we plan to make this version compatible with multiple versions,
    //this will help


    public static @Nullable Component getComponentRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (nms instanceof Component) return (Component)nms;
        return null;
    }



    public static @Nullable Rideable getRideableRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (!(nms instanceof Component comp)) return null;

        Construct body = comp.getBody();
        if (body instanceof Rideable ride) return ride;
        return null;
    }

    public static boolean isOperatorOf(Player player, Construct struct) {

        if (player == null) return false;

        if (!(struct instanceof Rideable ride)) return false;
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


//    public void x() {
//        net.minecraft.world.entity.Entity e;
//        e.inBlock();
//
//    }


    //see entity.inBlock()
        /*
        public boolean inBlock() {
        if (this.P) {
            return false;
        } else {
            float f = this.aW.a * 0.8F;
                            ^ width of the entity size

            AxisAlignedBB axisalignedbb = AxisAlignedBB.a(this.bb(), (double)f, 1.0E-6, (double)f);
            //bb() returns Vec3D(this.locX(), this.getHeadY(), this.locZ());

            t = world

            return this.t.b(this, axisalignedbb, (iblockdata, blockposition) -> {
                return iblockdata.o(this.t, blockposition);
            }).findAny().isPresent();
        }
    }
         */
}
