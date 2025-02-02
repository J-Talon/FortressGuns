package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class InventoryHandler implements Listener
{
    private static final Map<UUID, ConstructInventory> activeInventories = new HashMap<>();

    public static void startInteraction(Player player, ConstructInventory cons) {
        Inventory inv = cons.getInventory();
        player.closeInventory();
        System.out.println("active before: "+cons.getInventory() +" "+cons.getInventory().getSize());

        UUID id = player.getUniqueId();
        if (activeInventories.containsKey(id)) {
            activeInventories.replace(id, cons);
        }
        else
            activeInventories.put(id, cons);

        player.openInventory(inv);
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        UUID id = event.getPlayer().getUniqueId();
        Inventory inv = event.getInventory();
        ConstructInventory cons = activeInventories.getOrDefault(id, null);

        if (cons == null) {
            return;
        }

        Inventory consInv = cons.getInventory();
        if (!inv.equals(consInv)) {
            activeInventories.remove(id);
            return;
        }

        cons.onInventoryOpen(event);
    }


    public boolean isNotInventory(InventoryInteractEvent event) {
        UUID id = event.getWhoClicked().getUniqueId();
        InventoryView view = event.getView();

        if (!activeInventories.containsKey(id)) {
            return true;
        }

        Inventory cons = activeInventories.get(id).getInventory();


        if (!(view.getTopInventory().equals(cons)) && !(view.getBottomInventory().equals(cons))) {
            activeInventories.remove(id);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID id = event.getPlayer().getUniqueId();

        if (activeInventories.containsKey(id)) {
            activeInventories.get(id).onInventoryClose(event);
        }

        activeInventories.remove(id);
        //under any circumstance when an inventory is closed, we should end the interaction
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (isNotInventory(event))
            return;

        activeInventories.get(event.getWhoClicked().getUniqueId()).transact(event);

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isNotInventory(event))
            return;
        activeInventories.get(event.getWhoClicked().getUniqueId()).transact(event);
    }





}
