package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class BulkLoadingInventory extends TransactionReloadInventory {


    static final ItemStack BORDER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    static final ItemStack LOAD = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    static final ItemStack IN = new ItemStack(Material.RAIL);

    static {
        ItemMeta bord = BORDER.getItemMeta();
        ItemMeta load = LOAD.getItemMeta();
        ItemMeta in = IN.getItemMeta();

        bord.setDisplayName(ChatColor.GRAY+"");
        in.setDisplayName(ChatColor.GRAY+"");
        load.setDisplayName(ChatColor.GREEN+"Left click: Load | Right click: Unload");

        BORDER.setItemMeta(bord);
        LOAD.setItemMeta(load);
        IN.setItemMeta(in);

    }

    public BulkLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        init();
    }


    @Override
    protected void onItemPickup(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (isStaticItem(clicked)) {
            event.setCancelled(true);
        }

        if (!LOAD.isSimilar(clicked)) {
            return;
        }

        ItemStack residing = gui.getItem(getInputSlot());
        Artillery body = (Artillery)getOwner();
        AmmoItem input = ArtilleryItemHelper.isAmmoItem(residing);

        if (event.isRightClick()) {

            if (body.getAmmo() <= 0)
                return;

            //if the loaded type is not the same
            if (body.getLoadedAmmoType() != input && input != null) {
                return;
            }

            ///input == null || input is the same

            int exchange;
            if (input == null) {
                ItemStack ammoOut = ArtilleryItemHelper.createAmmoItem(body.getLoadedAmmoType());
                exchange = body.getAmmo() - ammoOut.getMaxStackSize();
                ammoOut.setAmount(exchange);
                gui.setItem(getInputSlot(),ammoOut);
            }
            else {
                exchange = body.getAmmo() - (residing.getMaxStackSize() - residing.getAmount());
                residing.setAmount(exchange);
                gui.setItem(getInputSlot(), residing);
            }

            body.setAmmo(body.getAmmo() - exchange);


        }
        else {

            if (input == null)
                return;


            if (!body.acceptsAmmo(input))
                return;

            AmmoItem loaded = body.getLoadedAmmoType();

            if (loaded != null && loaded != input)
                return;

            int difference = Math.min(body.getMaxAmmo() - body.getAmmo(), residing.getAmount());

            if (difference <= 0)
                return;

            int remainder = difference - residing.getAmount();
            if (remainder <= 0) {
                gui.setItem(getInputSlot(), new ItemStack(Material.AIR));
            }
            else {
                residing.setAmount(remainder);
                gui.setItem(getInputSlot(), residing);
            }

            body.setLoadedAmmoType(input);
            body.setAmmo(body.getAmmo() + difference);
            updateAppearance();
        }
    }


    @Override
    public void updateState() {
        if (gui.getViewers().isEmpty())
            return;

        updateAppearance();
    }

    @Override
    public void onInventoryOpen(InventoryOpenEvent event) {
        updateAppearance();
    }

    private void updateAppearance() {
        Artillery body = (Artillery)owner;

        int i;
        for (i = 3; i < Math.min(9, body.getAmmo()); i ++) {
            gui.setItem(i, IN);
        }

        for (;i < 9; i ++) {
            gui.setItem(i, BORDER);
        }
    }

    @Override
    public int getInputSlot() {
        return 1;
    }


    @Override
    public void init() {
        inLoadingAnimation = false;
        gui.clear();

        for (int i = 2; i < 9; i ++) {
            gui.setItem(i, BORDER);
        }

        gui.setItem(0, LOAD);
    }

    @Override
    protected boolean isStaticItem(ItemStack current) {
        return BORDER.isSimilar(current) || LOAD.isSimilar(current) || IN.isSimilar(current);
    }


    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();

        ItemStack loading = gui.getItem(getInputSlot());
        Map<Integer, ItemStack> remainder = player.getInventory().addItem(loading);
        gui.setItem(getInputSlot(), new ItemStack(Material.AIR));

        if (remainder.isEmpty()) {
            updateAppearance();
            return;
        }

        World world = player.getWorld();
        for (ItemStack item: remainder.values()) {
            world.dropItem(player.getLocation(),item);
        }

        updateAppearance();
    }
}
