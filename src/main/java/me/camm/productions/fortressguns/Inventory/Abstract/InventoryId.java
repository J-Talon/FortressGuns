package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Inventory.*;

public enum InventoryId {

    BULK("bulkLoading", BulkLoadingInventory.class),
    MENU_PRECISE("preciseMenu", PrecisionMenuInventory.class),
    MENU_ROUGH("roughMenu", RoughMenuInventory.class),
    JAM("jam", JamInventory.class),
    STANDARD("standardLoading", StandardLoadingInventory.class);

    private final String name;
    private final Class<? extends ConstructInventory> inv;

    InventoryId(String name, Class<? extends ConstructInventory> inv) {
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
