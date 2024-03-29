package me.camm.productions.fortressguns.Inventory;

public enum InventorySetting {
    RADAR(81,"Radar"),
    LOADING(9,"Loading..."),
    TRANSACTION(54, "Settings");

    public final int size;
    public final String title;

    InventorySetting(int size, String title) {
        this.size = size;
        this.title = title;
    }
}
