package me.camm.productions.fortressguns.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class PanelInventory extends TransactionInventory {

    protected Map<String, MenuFunction> functions;

    public PanelInventory(Construct owner, InventoryCategory setting, InventoryGroup group) {
        super(owner, setting, group);
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
        MenuInventory.MenuFunction function = functions.getOrDefault(name, null);
        if (function == null)
            return;

        System.out.println("menu function:"+name);

        function.onEvent(event, owner);
    }


    @Override
    protected boolean isStaticItem(ItemStack current) {
        for (StaticItem b: StaticItem.values()) {
            if (ConstructItemHelper.matchesName(current, b.getName()))
                return true;
        }
        return false;
    }


    @FunctionalInterface
    public interface MenuFunction {
        void onEvent(InventoryClickEvent event, Construct body);


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
}
