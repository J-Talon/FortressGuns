package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class JamInventory extends ConstructInventory {

    public JamInventory(RapidFire owner, InventoryGroup group) {
        super(owner, InventoryCategory.JAM_CLEAR, group);
    }


    @Override
    public void transact(InventoryDragEvent event) {

    }

    @Override
    public void transact(InventoryClickEvent event) {

    }

    @Override
    public void init() {

    }
}
