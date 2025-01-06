package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class BulkLoadingInventory extends TransactionInventory {


    public BulkLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, InventoryCategory.RELOADING, group);
    }



    @Override
    protected void onDrag(InventoryDragEvent event, @Nullable Inventory inv) {
        if (inv == null)
            return;
    }

    @Override
    protected void onDragAcross(InventoryDragEvent event) {

    }

    @Override
    protected void onItemDrop(InventoryClickEvent event) {

    }

    @Override
    protected void onItemMove(InventoryClickEvent event) {

    }

    @Override
    protected void onItemPickup(InventoryClickEvent event) {

    }

    @Override
    protected void onItemPlace(InventoryClickEvent event) {

    }

    @Override
    public void init() {

    }
}
