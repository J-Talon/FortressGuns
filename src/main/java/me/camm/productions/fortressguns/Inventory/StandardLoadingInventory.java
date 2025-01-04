package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import net.minecraft.util.Tuple;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;


public class StandardLoadingInventory extends TransactionInventory {

    static final ItemStack START = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    static final ItemStack STOP = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    static final ItemStack STICK = new ItemStack(Material.STICK);



    static {

        ItemMeta dest = START.getItemMeta();
        ItemMeta placeholder = STOP.getItemMeta();
        ItemMeta ramRod = STICK.getItemMeta();

            dest.setDisplayName(ChatColor.GREEN + "Load");
            placeholder.setDisplayName(ChatColor.GRAY + "");
            ramRod.setDisplayName(ChatColor.WHITE + "Ram rod");

            START.setItemMeta(dest);
            STOP.setItemMeta(placeholder);
            STICK.setItemMeta(ramRod);
    }

    private boolean isLoading;

    public StandardLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, InventoryCategory.RELOADING, group);
        init();
    }

    @Override
    public void transact(InventoryDragEvent event) {
        event.getType();
        event.setCancelled(true);
    }



    @Override
    public void init() {
        isLoading = false;
        gui.clear();
        for (int slot = 1; slot < gui.getSize()-1; slot ++) {
            gui.setItem(slot,STOP);
        }

        gui.setItem(gui.getSize()-1,START);
    }


    //cursor() --> cursor after done dragging
    //oldCursor() --> cursor before dragging
    @Override
    protected void onDrag(InventoryDragEvent event, @Nullable Inventory inv) {
        if (gui.equals(inv)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onDragAcross(InventoryDragEvent event) {
        // keep it simple
        event.setCancelled(true);
    }

    @Override
    protected void onItemDrop(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem();

        Inventory currentInv = event.getClickedInventory();

        if (gui.equals(currentInv)) {
            if (START.isSimilar(current) || STOP.isSimilar(current) || STICK.isSimilar(current)) {
                event.setCancelled(true);
                return;
            }

            if (isLoading)
                event.setCancelled(true);
        }
    }


    /*
getSlot() --> slot you are taking from
getCurrentItem() --> item that was in the inv you are taking from
getHotbarButton() --> the number on the numpad you pressed (not - 1 [THEY CHANGED IT???])
getClickedInventory() --> if you are swapping, then clickedInventory is the inv that your mouse was
hovering over when you tapped the numpad
 */
    @Override
    protected void onItemMove(InventoryClickEvent event) {

        System.out.println("on move");
        Artillery body = (Artillery)owner;

        InventoryView view = event.getView();
        Inventory clicked = event.getClickedInventory();
        ItemStack current = event.getCurrentItem();

        Inventory dest;
        ItemStack input;
        ItemStack residing;
        int hotbarButton = event.getHotbarButton();


        //this means that it's shift click etc
        if (hotbarButton < 0) {

            System.out.println("shift click");
            input = event.getCurrentItem();
            dest = (view.getTopInventory().equals(clicked)) ? view.getBottomInventory(): view.getTopInventory();

            if (isLoading || clicked == null) {
                event.setCancelled(true);
                return;
            }

            //they're shift clicking ammo to some inventory

            //whatever they're shifting isn't ammo
            //this means they're either trying to put some junk into the loading inv
            //or they're trying to take the buttons and stuff from the loading inv
            AmmoItem currAmmo = ArtilleryItemHelper.isAmmoItem(current);
            if (currAmmo == null) {
                event.setCancelled(true);
                return;
            }

            if (!body.acceptsAmmo(currAmmo)) {
                event.setCancelled(true);
                return;
            }

            //that's fine, they can shift click to their own inv
            if (dest.equals(view.getBottomInventory()))
                return;

            residing = gui.getItem(0);
            Tuple<ItemStack, ItemStack> res = merge(residing,input);

            if (res == null) {
                event.setCancelled(true);
                return;
            }

            gui.setItem(0,res.a());
            clicked.setItem(event.getSlot(),res.b());
            event.setCancelled(true);
        }
        else {
            //it's a swap with the hotbar
            Inventory playerInv = view.getBottomInventory();
            dest = event.getClickedInventory();
            input = playerInv.getItem(hotbarButton);
            System.out.println(hotbarButton);

            residing = current;

            //the source is always going to be their hotbar when they are placing into the top, but
            //the dest may be the gui or the player inv
            if (playerInv.equals(dest)) {
                return;
            }

            if (isLoading) {
                event.setCancelled(true);
                return;
            }


            AmmoItem inputAmmo = ArtilleryItemHelper.isAmmoItem(input);
            AmmoItem residingAmmo = ArtilleryItemHelper.isAmmoItem(residing);


            if (residingAmmo != null && (residingAmmo != inputAmmo)) {

                //they're bringing back to their own inv
                if (input == null || input.getType().isAir()) {
                    return;
                }

                event.setCancelled(true);
                return;
            }



            event.setCancelled(true);
            if (!body.acceptsAmmo(inputAmmo))
                return;


            Tuple<ItemStack, ItemStack> res = merge(residing, input);
            if (res == null) {
                return;
            }

            gui.setItem(0, res.a());
            playerInv.setItem(hotbarButton,res.b());
        }

    }


    /*
getSlot() --> slot you are taking from
getCurrentItem() --> the item picked up
getCursor() --> item on the cursor before the pickup | is Material.AIR generally if is empty
 */
    @Override
    protected void onItemPickup(InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();
        ItemStack current = event.getCurrentItem();


        if (START.isSimilar(current) || STOP.isSimilar(current) || STICK.isSimilar(current))
            event.setCancelled(true);

        if (!gui.equals(inv)) {
            //account for collect action
            return;
        }

        if (isLoading)
            return;

        //they're in the transaction inv trying to take ammo from it
        //allowed if not loading
        if (ArtilleryItemHelper.isAmmoItem(current) != null)  {
            return;
        }

        event.setCancelled(true);



        if (START.isSimilar(current)) {
            isLoading = true;
            Player player = (Player)event.getWhoClicked();


            new BukkitRunnable() {
                int slot = 0;
                boolean pushed = false;
                AmmoItem type = null;
                public void run() {

                    if (!pushed && slot < gui.getSize() - 2) {
                        ItemStack shell = gui.getItem(slot);
                        player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND,0.5f,1);

                        if (type == null) {
                            type = ArtilleryItemHelper.isAmmoItem(shell);
                        }

                        gui.setItem(slot + 1, shell);
                        gui.setItem(slot, STICK);

                        slot++;

                        if (slot >= gui.getSize() - 2)
                            pushed = true;
                    }
                    else {
                        gui.setItem(slot + 1, STOP);

                        if (type != null && slot == gui.getSize() - 2) {
                            ((Artillery) owner).setAmmo(((Artillery) owner).getAmmo() + 1);
                            player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE,1,2);
                        }
                        else
                            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT,0.5f,1);

                        slot --;
                    }

                    gui.setItem(gui.getSize() - 1, START);

                    if (pushed && slot < -1) {
                        isLoading = false;
                        gui.setItem(0, new ItemStack(Material.AIR));
                        cancel();
                    }
                }
            }.runTaskTimer(FortressGuns.getInstance(),0,2);
        }
    }



    /*
    getSlot() --> slot you are placing in
    getCurrentItem() --> the item in the slot before you placed the item in. Nullable
    getCursor() --> item on cursor before the place in
     */
    @Override
    protected void onItemPlace(InventoryClickEvent event) {

        System.out.println("on place");
        Inventory inv = event.getClickedInventory();
        if (event.getView().getBottomInventory().equals(inv) || inv == null) {
            return;
        }

        int slot = event.getSlot();
        if (slot != 0) {
            event.setCancelled(true);
            return;
        }

        if (isLoading) {
            event.setCancelled(true);
            return;
        }

        ItemStack cursor = event.getCursor();
        AmmoItem cursorAmmo = ArtilleryItemHelper.isAmmoItem(cursor);
        if (cursorAmmo == null || !((Artillery)owner).acceptsAmmo(cursorAmmo)) {
            event.setCancelled(true);
            return;
        }

        ItemStack residing = event.getCurrentItem();
        Tuple<ItemStack, ItemStack> res = merge(residing,cursor);

        event.setCancelled(true);

        if (res == null)
            return;


        inv.setItem(slot, res.a());
        event.getWhoClicked().setItemOnCursor(res.b());

    }




    @Override
    public void onInventoryClose() {
        isLoading = false;
    }
}
