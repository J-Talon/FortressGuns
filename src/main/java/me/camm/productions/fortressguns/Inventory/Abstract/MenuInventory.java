package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public abstract class MenuInventory extends TransactionInventory {

    public MenuInventory(Construct owner, InventoryGroup group) {
        super(owner,InventoryCategory.MENU, group);
    }

    @Override
    protected void onDrag(InventoryDragEvent event, @Nullable Inventory inv) {

    }

    @Override
    protected void onDragAcross(InventoryDragEvent event) {

    }

    @Override
    protected void onItemDrop(InventoryClickEvent event) {

    }

    @Override
    protected void onHotbarItemMove(InventoryClickEvent event) {

    }

    @Override
    protected void onShiftMove(InventoryClickEvent event) {

    }

    @Override
    protected void onItemPickup(InventoryClickEvent event) {

    }

    @Override
    protected void onItemPlace(InventoryClickEvent event) {

    }
}
