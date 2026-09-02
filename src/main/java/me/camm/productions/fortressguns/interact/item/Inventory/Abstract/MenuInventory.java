package me.camm.productions.fortressguns.interact.item.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;


public abstract class MenuInventory extends PanelInventory {

    public MenuInventory(Construct owner, InventoryGroup group) {
        super(owner,InventoryCategory.MENU, group);
    }



    protected static MenuFunction openReloading = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {

            Player player = (Player)event.getWhoClicked();
            Artillery art = (Artillery) body;

            art.getInventoryGroup().openInventory(InventoryCategory.RELOADING,player);
        }
    };


   protected static MenuFunction disassemble = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            body.destroy(!body.isInvalid() && body.chunkLoaded(), false);
        }
    };



}




