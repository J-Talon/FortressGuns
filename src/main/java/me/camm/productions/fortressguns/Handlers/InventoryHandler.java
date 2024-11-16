package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Inventory.ConstructInventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;



public class InventoryHandler implements Listener
{

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

           Inventory inv = event.getView().getTopInventory();
           InventoryHolder holder = inv.getHolder();

           if (holder == null) {
               return;
           }

           if (!(holder instanceof Artillery)) {
               return;
           }

         Artillery arty = (Artillery)inv.getHolder();
         ConstructInventory transaction = arty.getLoadingInventory();
         transaction.transact(event);

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

    }

}
