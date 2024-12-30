package me.camm.productions.fortressguns.Inventory.Abstract;

public enum InventoryCategory {
    RADAR(81,"Radar"),
    RELOADING(9,"Loading..."),
    JAM_CLEAR(27, "Clearing Jam..."),
    MENU(54, "Settings");

    public final int size;
    public final String title;

    InventoryCategory(int size, String title) {
        this.size = size;
        this.title = title;
    }
}
