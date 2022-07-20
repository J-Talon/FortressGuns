package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class TuningInventory extends ArtilleryInventory {


    public TuningInventory(Artillery owner, InventorySetting setting) {
        super(owner, setting);
    }

    @Override
    public void transact(InventoryDragEvent event) {
        int size = gui.getSize();
        Set<Integer> slots = event.getRawSlots();
        for (int current: slots) {
            if (current < size) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void transact(InventoryClickEvent event) {




    }

    @Override
    public void init() {

    }
}
