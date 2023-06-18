package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class StandardLoadingInventory extends ArtilleryInventory {

    static final ItemStack DESTINATION = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    static final ItemStack PROPELLANT = new ItemStack(Material.GUNPOWDER);
    static final ItemStack PROJECTILE = new ItemStack(Material.LEVER);
    static {

        ItemMeta dest = DESTINATION.getItemMeta();
        ItemMeta prop = PROPELLANT.getItemMeta();
        ItemMeta proj = PROJECTILE.getItemMeta();

            dest.setDisplayName(ChatColor.GREEN + "Finish");
            prop.setDisplayName(ChatColor.GRAY + "Propellant");
            proj.setDisplayName(ChatColor.WHITE + "Shell");

            DESTINATION.setItemMeta(dest);
            PROPELLANT.setItemMeta(prop);
            PROJECTILE.setItemMeta(proj);
    }

    public StandardLoadingInventory(Artillery owner) {
        super(owner, InventorySetting.LOADING);
        init();
    }

    @Override
    public void transact(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void transact(InventoryClickEvent event) {
       Inventory inv = event.getClickedInventory();
       if (inv == null) {
           return;
       }
       if (!inv.equals(gui))
           return;

       event.setCancelled(true);

       ItemStack stack = event.getCurrentItem();
       if (stack == null || stack.getItemMeta() == null)
           return;

       if (stack.isSimilar(DESTINATION))
           return;

       int currentSlot = event.getSlot();
       int nextSlot = ++currentSlot;

       ItemStack next = gui.getItem(nextSlot);

       if (next == null || next.getItemMeta() == null) {
           gui.setItem(currentSlot,null);
           gui.setItem(nextSlot, stack);

           HumanEntity e = event.getWhoClicked();
           if (e instanceof Player){
               Player p = (Player)e;
               p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
           }


       }
       else {

           ItemStack end = gui.getItem(currentSlot + 2);
           if (PROPELLANT.isSimilar(next) && DESTINATION.isSimilar(end) ) {
               owner.setBullets(1);

               for (HumanEntity e: gui.getViewers()) {
                   e.closeInventory();
                   e.sendMessage(ChatColor.GRAY+"Artillery is loaded with 1 shell.");

                   if (e instanceof Player) {
                       Player p = (Player)e;
                       p.playSound(p.getLocation(),Sound.BLOCK_IRON_DOOR_CLOSE,1,2);
                   }
               }

               init();
           }
       }





    }

    @Override
    public void init() {

        gui.clear();
        gui.setItem(7,DESTINATION);
        gui.setItem(1,PROJECTILE);
        gui.setItem(0,PROPELLANT);
    }
}
