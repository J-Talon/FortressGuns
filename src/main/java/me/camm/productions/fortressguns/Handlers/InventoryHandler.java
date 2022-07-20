package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Inventory.ArtilleryInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.UUID;

public class InventoryHandler implements Listener
{

    private static HashMap<UUID, Inventory> operators = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {


       HumanEntity entity =  event.getWhoClicked();
       UUID id = entity.getUniqueId();

       if (operators.containsKey(id)) {
           Inventory inv = operators.get(id);

           InventoryHolder holder = inv.getHolder();
           if (holder == null) {
               throw new IllegalStateException("An artillery inventory is not tied to any artillery!");
           }

           if (!(holder instanceof Artillery)) {
               throw new IllegalStateException("An artillery inventory does not belong to an artillery piece!");
           }

           Artillery arty = (Artillery)inv.getHolder();
          ArtilleryInventory transaction = arty.getArtyInventory();
          transaction.transact(event);

       }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

    }


    public void update(Player player, Artillery which) {


        UUID id = player.getUniqueId();
        Inventory inv = which.getInventory();
        if (operators.containsKey(id)) {
            operators.replace(id, inv);
        }
        else  operators.put(id, inv);
    }

}
