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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;


public class StandardLoadingInventory extends TransactionReloadInventory {

    static final ItemStack LOAD = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    static final ItemStack BLOCK = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
    static final ItemStack STICK = new ItemStack(Material.STICK);


    private ItemStack loading;




    static {

        ItemMeta dest = LOAD.getItemMeta();
        ItemMeta placeholder = BLOCK.getItemMeta();
        ItemMeta ramRod = STICK.getItemMeta();

            dest.setDisplayName(ChatColor.GREEN + "Left click: Load | Right click: Unload");
            placeholder.setDisplayName(ChatColor.GRAY + "");
            ramRod.setDisplayName(ChatColor.WHITE + "Ram rod");


            LOAD.setItemMeta(dest);
            BLOCK.setItemMeta(placeholder);
            STICK.setItemMeta(ramRod);

    }



    public StandardLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        init();
    }


    @Override
    public void init() {
        inLoadingAnimation = false;
        gui.clear();
        for (int slot = 1; slot < gui.getSize()-1; slot ++) {
            gui.setItem(slot, BLOCK);
        }

        gui.setItem(gui.getSize()-1, LOAD);
        loading = null;
    }


    @Override
    public int getInputSlot() {
        return 0;
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


        if (isStaticItem(current))
            event.setCancelled(true);

        if (!gui.equals(inv)) {
            //account for collect action
            return;
        }

        if (inLoadingAnimation)
            return;

        //they're in the transaction inv trying to take ammo from it
        //allowed if not loading
        if (ArtilleryItemHelper.isAmmoItem(current) != null)  {
            return;
        }

        event.setCancelled(true);

        if (LOAD.isSimilar(current)) {

            Artillery body = (Artillery) owner;
            Player player = (Player) event.getWhoClicked();

            if (event.isLeftClick()) {
                inLoadingAnimation = true;


                new BukkitRunnable() {
                    int slot = 0;
                    boolean pushed = false;
                    AmmoItem type = null;

                    public void run() {


                        if (gui.getViewers().isEmpty()) {
                            inLoadingAnimation = false;
                            loading = null;
                            cancel();
                            return;
                        }

                        if (!pushed && slot < gui.getSize() - 2) {
                            ItemStack shell = gui.getItem(slot);
                            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.5f, 1);

                            if (type == null) {
                                type = ArtilleryItemHelper.isAmmoItem(shell);

                                if (type != null)
                                    loading = shell;
                            }

                            ItemStack next = gui.getItem(slot + 1);
                            if (next == null || BLOCK.isSimilar(next))
                                gui.setItem(slot + 1, shell);

                            gui.setItem(slot, STICK);

                            slot++;

                            if (slot >= gui.getSize() - 2)
                                pushed = true;
                        } else {
                            gui.setItem(Math.max(slot,0), BLOCK);

                            if (type != null && slot == gui.getSize() - 2) {
                                body.setAmmo(body.getAmmo() + 1);
                                body.setLoadedAmmoType(type);

                                player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 2);
                            } else
                                player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.5f, 1);

                            slot--;
                        }

                        if (pushed && slot < -1) {
                            inLoadingAnimation = false;
                            gui.setItem(0, new ItemStack(Material.AIR));
                            loading = null;
                            cancel();
                        }
                    }
                }.runTaskTimer(FortressGuns.getInstance(), 0, 2);
            }
            else {

                int loadedAmount = body.getAmmo();
                AmmoItem loaded = body.getLoadedAmmoType();
                if (loadedAmount <= 0 || inLoadingAnimation) {
                    return;
                }

                if (loaded == null) {
                    return;
                }

                ItemStack ammo = ArtilleryItemHelper.createAmmoItem(loaded);
                ItemStack residing = gui.getItem(0);

                if (residing == null || residing.getType().isAir()) {
                    body.setAmmo(loadedAmount - 1);
                    gui.setItem(0, ammo);
                    return;
                }

                if (!ammo.isSimilar(residing)) {
                    return;
                }

                residing.setAmount(residing.getAmount() + 1);
                gui.setItem(0, residing);
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 2);
            }
        }
    }





    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        if (inLoadingAnimation) {
            Player player = (Player)event.getPlayer();

            if (loading == null) {
                init();
                return;
            }

            Map<Integer, ItemStack> remainder = player.getInventory().addItem(loading);

            if (remainder.isEmpty()) {
                init();
                return;
            }

            World world = player.getWorld();
            for (ItemStack item: remainder.values()) {
                world.dropItem(player.getLocation(),item);
            }

            init();
        }
    }


    @Override
    protected boolean isStaticItem(ItemStack current) {
        return LOAD.isSimilar(current) || BLOCK.isSimilar(current) || STICK.isSimilar(current);
    }
}
