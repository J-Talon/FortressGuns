package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
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
     protected final Construct owner;
     protected InventoryGroup group;

     protected InventoryCategory tag;

     public ConstructInventory(Construct owner, InventoryCategory setting, InventoryGroup group) {
          this.owner = owner;
          this.group = group;
          this.gui = Bukkit.createInventory(null, setting.size, setting.title);
          this.tag = setting;
     }


     public InventoryCategory getTag() {
          return tag;
     }


     public Inventory getInventory(){
          return gui;
     }

     public Construct getOwner(){
          return owner;
     }

     public abstract void transact(InventoryDragEvent event);

     public abstract void transact(InventoryClickEvent event);

     public abstract void init();

     public void onInventoryClose() {

     }


}
