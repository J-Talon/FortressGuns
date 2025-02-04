package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import net.minecraft.util.Tuple;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class TransactionReloadInventory extends TransactionInventory {

    protected boolean inLoadingAnimation;

    public TransactionReloadInventory(Construct owner, InventoryGroup group) {
        super(owner, InventoryCategory.RELOADING, group);
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

            if (isStaticItem(current)) {
                event.setCancelled(true);
                return;
            }

            if (inLoadingAnimation)
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
    protected void onHotbarItemMove(InventoryClickEvent event) {

        Artillery body = (Artillery)owner;

        InventoryView view = event.getView();
        ItemStack current = event.getCurrentItem();

        Inventory dest;
        ItemStack input;
        ItemStack residing;
        int hotbarButton = event.getHotbarButton();

        //it's a swap with the hotbar
        Inventory playerInv = view.getBottomInventory();
        dest = event.getClickedInventory();
        input = playerInv.getItem(hotbarButton);

        residing = current;

        //the source is always going to be their hotbar when they are placing into the top, but
        //the dest may be the gui or the player inv
        if (playerInv.equals(dest)) {
            return;
        }

        if (inLoadingAnimation) {
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


        Tuple<ItemStack, ItemStack> res = mergeAmmo(residing, input);
        if (res == null) {
            return;
        }

        if (res.a().getType() == Material.AIR) {
            onAmmoFull(event);
        }

        gui.setItem(getInputSlot(), res.a());
        playerInv.setItem(hotbarButton,res.b());
    }




    @Override
    protected void onShiftMove(InventoryClickEvent event) {
        Artillery body = (Artillery)owner;

        InventoryView view = event.getView();
        Inventory clicked = event.getClickedInventory();
        ItemStack current = event.getCurrentItem();

        Inventory dest;
        ItemStack input;
        ItemStack residing;


        input = event.getCurrentItem();
        dest = (view.getTopInventory().equals(clicked)) ? view.getBottomInventory(): view.getTopInventory();

        if (inLoadingAnimation || clicked == null) {
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

        residing = gui.getItem(getInputSlot());
        Tuple<ItemStack, ItemStack> res = mergeAmmo(residing,input);

        if (res == null) {
            event.setCancelled(true);
            return;
        }

        if (res.a().getType() == Material.AIR) {
            onAmmoFull(event);
        }

        gui.setItem(getInputSlot(),res.a());
        clicked.setItem(event.getSlot(),res.b());
        event.setCancelled(true);
    }


    private void onAmmoFull(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.BLOCKS,1,1);
        player.sendMessage(ChatColor.RED+" [!] The gun's ammo capacity is full!");

    }


    /*
 getSlot() --> slot you are placing in
 getCurrentItem() --> the item in the slot before you placed the item in. Nullable
 getCursor() --> item on cursor before the place in
  */
    @Override
    protected void onItemPlace(InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();
        if (event.getView().getBottomInventory().equals(inv) || inv == null) {
            return;
        }

        int slot = event.getSlot();
        if (slot != getInputSlot()) {
            event.setCancelled(true);
            return;
        }

        if (inLoadingAnimation) {
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
        Tuple<ItemStack, ItemStack> res = mergeAmmo(residing,cursor);

        event.setCancelled(true);

        if (res == null)
            return;

        inv.setItem(slot, res.a());
        if (res.a().getType() == Material.AIR) {
            onAmmoFull(event);
        }

        event.getWhoClicked().setItemOnCursor(res.b());

    }


    public abstract int getInputSlot();
}
