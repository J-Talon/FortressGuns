package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemHelper;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class BulkLoadingInventory extends TransactionReloadInventory {


    static final ItemStack BORDER;
    static final ItemStack LOAD;

    static {
        BORDER = StaticItem.BORDER.toItemRaw();
        LOAD = StaticItem.LOAD_UNLOAD.toItemRaw();

    }

    public BulkLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        init();
    }


    @Override
    protected void onItemPickup(InventoryClickEvent event) {

        Player player = (Player)event.getWhoClicked();
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

        player.playSound(player.getLocation(),Sound.BLOCK_PISTON_CONTRACT,SoundCategory.BLOCKS,1,1);

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
            updateState();


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

            if (difference <= 0) {
                return;
            }

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
            updateState();
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
        ItemStack curr = currentLoaded();
        int i;
        for (i = 3; i < Math.min(9, body.getAmmo()); i ++) {
            gui.setItem(i, curr);
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


    private ItemStack currentLoaded() {
        Artillery owner = (Artillery)getOwner();
        AmmoItem item = owner.getLoadedAmmoType();
        String lore;
        if (owner.getAmmo() <= 0 || item == null) {
            lore = ChatColor.RED+"None";
        }
        else
            lore = ChatColor.GOLD + ""+item.getName();

        return StaticItem.ROUND_SHOWCASE.toItem(ChatColor.WHITE+"Loaded Ammo: "+lore);
    }

    @Override
    protected boolean isStaticItem(ItemStack current) {
        return BORDER.isSimilar(current) || LOAD.isSimilar(current) || currentLoaded().isSimilar(current);
    }


    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        player.playSound(player.getLocation(),Sound.BLOCK_IRON_TRAPDOOR_CLOSE,SoundCategory.BLOCKS,1,2);

        ItemStack loading = gui.getItem(getInputSlot());
        if (loading == null) {
            updateAppearance();
            return;
        }

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
