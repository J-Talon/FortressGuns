package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class RadarInventory extends ArtilleryInventory {

    public RadarInventory(Artillery owner, InventorySetting setting) {
        super(owner, setting);
    }

    @Override
    public void transact(InventoryDragEvent event) {

    }

    @Override
    public void transact(InventoryClickEvent event) {

    }

    @Override
    public void init(){

    }
}
