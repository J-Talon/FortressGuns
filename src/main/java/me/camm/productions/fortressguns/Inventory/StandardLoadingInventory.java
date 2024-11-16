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

import java.util.ArrayList;


public class StandardLoadingInventory extends ConstructInventory {

    static final ItemStack DESTINATION = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    static final ItemStack PROPELLANT = new ItemStack(Material.GUNPOWDER);
    static final ItemStack PROJECTILE = new ItemStack(Material.LEVER);

    static final ItemStack AIR = new ItemStack(Material.AIR);
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

    private boolean addedProp;

    public StandardLoadingInventory(Artillery owner) {
        super(owner, InventorySetting.LOADING);
        init();
    }

    @Override
    public void transact(InventoryDragEvent event) {
        System.out.println("on transact drag event");
        event.setCancelled(true);
    }

    @Override
    public void transact(InventoryClickEvent event) {

        System.out.println("on transact click event");


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

       int currentSlot = event.getSlot();
       int nextSlot = currentSlot + 1;

       if (nextSlot > gui.getSize()-1) {
           ItemStack zeroth = gui.getItem(0);
           if (!addedProp && (zeroth == null || zeroth.getItemMeta() == null)) {
               gui.setItem(0,PROPELLANT);
               addedProp = true;
           }

           return;
       }

       ItemStack next = gui.getItem(nextSlot);

       if (next == null || next.getItemMeta() == null) {
           gui.setItem(nextSlot, stack);
           gui.setItem(currentSlot, null);

           HumanEntity e = event.getWhoClicked();
           if (e instanceof Player){
               Player p = (Player)e;
               p.playSound(p.getLocation(), Sound.BLOCK_PISTON_EXTEND,1,1);
           }
       }
       else {
           if (PROPELLANT.isSimilar(stack) && PROJECTILE.isSimilar(next) && PROJECTILE.isSimilar(gui.getItem(gui.getSize()-1))) {
               owner.setBullets(1);
               for (HumanEntity e: new ArrayList<>(gui.getViewers())) {
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
        addedProp = false;
        gui.clear();
        gui.setItem(0,PROJECTILE);
    }
}
