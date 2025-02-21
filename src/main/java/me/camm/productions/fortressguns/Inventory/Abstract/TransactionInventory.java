package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import net.minecraft.util.Tuple;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public abstract class TransactionInventory extends ConstructInventory {

    public TransactionInventory(Construct owner, InventoryCategory setting, InventoryGroup group) {
        super(owner, setting, group);
    }


    @Override
    public void transact(InventoryDragEvent event) {

        Set<Integer> slots = event.getRawSlots();

        if (slots.isEmpty())
            return;

        Integer[] arr = slots.toArray(new Integer[0]);
        int min, max;
        min = max = arr[0];

        for (int current: arr) {
            if (current > max) {
                max = current;
                continue;
            }

            if (current < min)
                min = current;
        }

        InventoryView view = event.getView();

        Inventory first = null, second = null;
        first = view.getInventory(min);
        second = view.getInventory(max);

        if (first == null) {
            onDrag(event,second);
            return;
        }

        if (second == null) {
            onDrag(event, first);
            return;
        }

        if (first.equals(second)) {
            onDrag(event, first);
            return;
        }

        onDragAcross(event);

    }

    @Override
    public void transact(InventoryClickEvent event) {

        String actionName = event.getAction().toString().toLowerCase();

        if (actionName.contains("drop")) {
            onItemDrop(event);
            return;
        }

        /*
        Taking with the mouse or swapping with the mouse
         */
        if (actionName.contains("pickup") || actionName.contains("collect") || actionName.contains("with")) {
            onItemPickup(event);
            return;
        }

        if (actionName.contains("swap") || actionName.contains("move") || actionName.contains("readd")) {
            onItemMove(event);
            return;
        }

        if (actionName.contains("hotbar") || actionName.contains("place")) {
            onItemPlace(event);
            return;
        }

        event.setCancelled(true);
    }




    /*
return: tuple<A,B> where:
A is the new residing item
B is the remainder from the transaction

return null? --> stacks cannot be merged
 */
    protected @Nullable Tuple<ItemStack, ItemStack> mergeAmmo(@Nullable ItemStack residing, ItemStack input) {

        AmmoItem resAmmo = ArtilleryItemHelper.isAmmoItem(residing);
        AmmoItem inputAmmo = ArtilleryItemHelper.isAmmoItem(input);
        ItemStack placed; //the itemstack placed into the gui
        ItemStack AIR = new ItemStack(Material.AIR);

        int addition, remaining;
        if (residing == null || residing.getType().isAir()) {

            if (inputAmmo == null)
                return null;

            addition = getAdditionDifference(input);
            placed = input.clone();
            placed.setAmount(addition);

            remaining = input.getAmount() - addition;
            if (remaining == 0) {
                return new Tuple<>(placed, AIR);
            }
            else {
                input.setAmount(remaining);
                return new Tuple<>(placed, input);
            }

        }
        else {
            if (resAmmo != inputAmmo)
                return null;

            addition = getAdditionDifference(residing, input);
            remaining = input.getAmount() - addition;
            residing.setAmount(residing.getAmount() + addition);

            if (remaining == 0)
                input = AIR;
            else
                input.setAmount(input.getAmount() - addition);

        }
        return new Tuple<>(residing, input);
    }

    /*
    @pre: both are ammo items of the same type, both non null
    calculates the remaining amount of item left from stacking into the artillery's max amount
     */
    protected int getAdditionDifference(ItemStack residing, ItemStack incoming) {

        Artillery body = (Artillery) owner;
        int allowedTotal;

        if (body.getMaxAmmo() < 0) {
            allowedTotal = residing.getMaxStackSize();
        }
        else
            allowedTotal = body.getMaxAmmo() - body.getAmmo();

        if (allowedTotal <= 0)
            return 0;

        int remainingAllowed = allowedTotal - residing.getAmount();
        if (remainingAllowed <= 0)
            return 0;

        remainingAllowed = Math.min(remainingAllowed, incoming.getAmount());

        int amountToFull = residing.getMaxStackSize() - residing.getAmount();
        return Math.min(remainingAllowed, amountToFull);

    }

    /*
    @pre residing is null
    calculates the remaining amount of item left from stacking into the artillery's max amount
     */
    protected int getAdditionDifference(ItemStack incoming) {
        Artillery body = (Artillery) owner;
        int allowedTotal = body.getMaxAmmo() < 0 ? incoming.getMaxStackSize() :body.getMaxAmmo() - body.getAmmo();
        return Math.min(incoming.getAmount(), allowedTotal);
    }






    //when the item is being dragged in the same inventory
    protected abstract void onDrag(InventoryDragEvent event, @Nullable Inventory inv);

    //when an item is being dragged across inventories
    protected abstract void onDragAcross(InventoryDragEvent event);

    //when the item is dropped out of the inv
    /*
    getSlot() --> slot which you are dropping from. -999 if NA
    getCurrentItem() --> item you are dropping before you dropped
     */
    protected abstract void onItemDrop(InventoryClickEvent event);


    /*
    getSlot() --> slot you are taking from
    getCurrentItem() --> item that was in the inv you are taking from
    getHotbarButton() --> the number on the numpad you pressed
    getClickedInventory() --> if you are swapping, then clickedInventory is the inv that your mouse was
    hovering over when you tapped the numpad
     */
    protected void onItemMove(InventoryClickEvent event) {

        if (event.getHotbarButton() < 0 && event.isShiftClick()) {
            onShiftMove(event);
        }
        else {
            onHotbarItemMove(event);
        }
    }


    protected abstract void onHotbarItemMove(InventoryClickEvent event);
    protected abstract void onShiftMove(InventoryClickEvent event);



    /*
    getSlot() --> slot you are taking from
    getCurrentItem() --> the item picked up
    getCursor() --> item on the cursor before the pickup | is Material.AIR generally if is empty
     */
    protected abstract void onItemPickup(InventoryClickEvent event);


    /*
    getSlot() --> slot you are placing in
    getCurrentItem() --> the item in the slot before you placed the item in. Nullable
    getCursor() --> item on cursor before the place in
     */
    protected abstract void onItemPlace(InventoryClickEvent event);


    /*
    returns whether this is a static item in the inventory and shouldn't be moved
    by the player under any circumstances
     */
    protected abstract boolean isStaticItem(ItemStack current);



}
