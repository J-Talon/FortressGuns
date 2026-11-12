package me.camm.productions.fortressguns.interact.item.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.PanelInventory;

public class RepairInventory extends PanelInventory {


    public RepairInventory(Construct owner, InventoryCategory setting, InventoryGroup group) {
        super(owner, setting, group);
    }

    @Override
    public void init() {

    }
}
