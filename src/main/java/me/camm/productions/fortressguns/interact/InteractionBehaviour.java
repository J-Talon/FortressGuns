package me.camm.productions.fortressguns.interact;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface InteractionBehaviour<T> {


    public abstract boolean accept(T item);

    public @Nullable default IBHandle getHandle() {return null;}




//    default void openMenu(Construct cons, Player player, ItemStack stack) {
//        if (!(cons instanceof Artillery body)) return;
//
//        ConstructInventory menu;
//        InventoryGroup group = body.getInventoryGroup();
//        FIND_INV:
//        {
//
//            if (body instanceof RapidFire rapid && rapid.isJammed()) {
//                menu = group.getInventoryByCategory(InventoryCategory.JAM_CLEAR);
//                break FIND_INV;
//            }
//
//            if (ItemUtils.isAmmoItem(stack) != null) {
//                menu = group.getInventoryByCategory(InventoryCategory.RELOADING);
//            } else {
//                menu = group.getInventoryByCategory(InventoryCategory.MENU);
//            }
//        }
//
//        if (menu == null) {
//            FortressGuns.getInstance().getLogger().warning("Inventory instance returned null!");
//            return;
//        }
//        group.openInventory(menu, player);
//    }
}
