package me.camm.productions.fortressguns.interact.item.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.MenuInventory;

public class RepairInventory extends MenuInventory {


    public RepairInventory(Construct owner, InventoryGroup group) {
        super(owner, group);
    }

    @Override
    public void init() {

    }
}
