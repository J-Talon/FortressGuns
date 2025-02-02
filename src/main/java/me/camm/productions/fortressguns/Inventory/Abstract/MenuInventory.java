package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuInventory extends TransactionInventory {

    protected Map<String, MenuFunction> functions;

    public MenuInventory(Construct owner, InventoryGroup group) {
        super(owner,InventoryCategory.MENU, group);
        functions = new HashMap<>();
    }

    @Override
    protected void onDrag(InventoryDragEvent event, @Nullable Inventory inv) {
        if (gui.equals(inv))
            event.setCancelled(true);
    }

    @Override
    protected void onDragAcross(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected void onItemDrop(InventoryClickEvent event) {

        ItemStack current = event.getCurrentItem();
        if (isStaticItem(current) && gui.equals(event.getClickedInventory()))
            event.setCancelled(true);
    }

    @Override
    protected void onHotbarItemMove(InventoryClickEvent event) {
        event.setCancelled(true);
        if (gui.equals(event.getClickedInventory())) {
            onButtonPress(event);
        }
    }

    @Override
    protected void onShiftMove(InventoryClickEvent event) {
        event.setCancelled(true);
        if (gui.equals(event.getClickedInventory())) {
            onButtonPress(event);
        }
    }

    @Override
    protected void onItemPickup(InventoryClickEvent event) {
        if (gui.equals(event.getClickedInventory())) {
            onButtonPress(event);
            event.setCancelled(true);
        }
    }

    @Override
    protected void onItemPlace(InventoryClickEvent event) {
        if (gui.equals(event.getClickedInventory())) {
            event.setCancelled(true);
            onButtonPress(event);
        }
    }

    protected void onButtonPress(InventoryClickEvent event) {
        ItemStack stack = event.getCurrentItem();
        if (stack == null)
            return;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return;

        String name = meta.getDisplayName();
        MenuFunction function = functions.getOrDefault(name, null);
        if (function == null)
            return;

        function.onEvent(event, owner);
    }


    @FunctionalInterface
    public interface MenuFunction {
        public void onEvent(InventoryClickEvent event, Construct body);


        default boolean preconditions(@Nullable ItemStack stack) {

            if (stack == null)
                return false;

            ItemMeta meta = stack.getItemMeta();
            return meta != null && meta.getLore() != null && !meta.getLore().isEmpty();
        }


        default double convert(String value) {
            try {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }

    }


    protected static MenuFunction openReloading = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            ((Artillery)body)
                    .getInventoryGroup()
                    .openInventory(InventoryCategory.RELOADING,(Player)event.getWhoClicked());
        }
    };


   protected static MenuFunction disassemble = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            ((Artillery)body).remove(true,false);
        }
    };



}




