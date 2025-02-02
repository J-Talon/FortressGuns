package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Inventory.Abstract.MenuInventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class RoughMenuInventory extends MenuInventory {

    public RoughMenuInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
    }

    @Override
    public void init() {

    }

    @Override
    protected boolean isStaticItem(ItemStack current) {
        return false;
    }
}
