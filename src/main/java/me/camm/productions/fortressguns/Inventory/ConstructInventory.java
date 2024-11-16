package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author CAMM
 */
public abstract class ConstructInventory
{
     protected final Inventory gui;
     protected final Artillery owner;

     public ConstructInventory(Artillery owner, InventorySetting setting) {
          this.owner = owner;
          this.gui = Bukkit.createInventory(owner, setting.size, setting.title);
     }

     public Inventory getInventory(){
          return gui;
     }

     public Artillery getOwner(){
          return owner;
     }


     public abstract void transact(InventoryDragEvent event);



     public abstract void transact(InventoryClickEvent event);


     public abstract void init();



}
