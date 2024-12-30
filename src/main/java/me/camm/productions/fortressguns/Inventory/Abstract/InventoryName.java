package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Inventory.BulkLoadingInventory;
import me.camm.productions.fortressguns.Inventory.PrecisionMenuInventory;
import me.camm.productions.fortressguns.Inventory.StandardLoadingInventory;

public enum InventoryName {

    BULK("bulkLoading", BulkLoadingInventory.class),
    MENU_PRECISE("preciseMenu", PrecisionMenuInventory.class),
    STANDARD("standardLoading", StandardLoadingInventory.class);

    private final String name;
    private final Class<? extends ConstructInventory> inv;

    private InventoryName(String name, Class<? extends ConstructInventory> inv) {
        this.name = name;
        this.inv = inv;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Class<? extends ConstructInventory> getInv() {
        return inv;
    }
}
