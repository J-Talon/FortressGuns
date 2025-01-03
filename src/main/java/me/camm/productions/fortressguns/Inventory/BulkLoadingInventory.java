package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryName;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class BulkLoadingInventory extends ConstructInventory {


    public BulkLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, InventoryCategory.RELOADING, group);
    }

    @Override
    public void transact(InventoryDragEvent event) {

    }

    @Override
    public String getName() {
        return InventoryName.BULK.toString();
    }

    @Override
    public void transact(InventoryClickEvent event) {

    }

    @Override
    public void init() {

    }
}
