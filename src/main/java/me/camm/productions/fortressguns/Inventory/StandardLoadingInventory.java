package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Inventory.Abstract.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;


public class StandardLoadingInventory extends TransactionReloadInventory {

    static final ItemStack LOAD;
    static final ItemStack BLOCK;
    static final ItemStack STICK;


    private ItemStack loading;
    private volatile boolean loaded;


    static {
        LOAD = StaticItem.LOAD_UNLOAD.toItemRaw();
        BLOCK = StaticItem.BORDER.toItemRaw();
        STICK = StaticItem.RAMROD.toItemRaw();
    }



    public StandardLoadingInventory(Artillery owner, InventoryGroup group) {
        super(owner, group);
        init();

    }


    @Override
    public void init() {

        inLoadingAnimation = false;
        loaded = false;
        gui.clear();
        for (int slot = 1; slot < gui.getSize()-1; slot ++) {
            gui.setItem(slot, BLOCK);
        }

        gui.setItem(gui.getSize()-1, LOAD);
        updateState();
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
        if (ConstructItemHelper.isAmmoItem(current) != null)  {
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

                        int end = gui.getSize() - 3;


                        if (!pushed && slot < end) {
                            ItemStack shell = gui.getItem(slot);
                            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.5f, 1);

                            if (type == null) {
                                type = ConstructItemHelper.isAmmoItem(shell);

                                if (type != null)
                                    loading = shell;
                            }

                            ItemStack next = gui.getItem(slot + 1);
                            if (next == null || BLOCK.isSimilar(next))
                                gui.setItem(slot + 1, shell);

                            gui.setItem(slot, STICK);

                            slot++;

                            if (slot >= end)
                                pushed = true;
                        } else {
                            gui.setItem(Math.max(slot,0), BLOCK);

                            if (type != null && slot == end) {
                                body.setAmmo(body.getAmmo() + loading.getAmount());
                                body.setLoadedAmmoType(type);
                                loaded = true;

                                updateState();

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

                ItemStack ammo = ConstructItemHelper.createAmmoItem(loaded);
                ItemStack residing = gui.getItem(0);

                if (residing == null || residing.getType().isAir()) {
                    body.setAmmo(loadedAmount - 1);
                    gui.setItem(0, ammo);

                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                    updateState();

                    return;
                }

                if (!ammo.isSimilar(residing)) {
                    return;
                }

                residing.setAmount(residing.getAmount() + 1);
                gui.setItem(0, residing);
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                updateState();
            }
        }
    }





    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

        Player player = (Player)event.getPlayer();
        player.playSound(player.getLocation(),Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS,1,2);


        Map<Integer, ItemStack> remainder;
        if (inLoadingAnimation) {
            if (loading == null) {
                init();
                return;
            }

            if (loaded) {
                init();
                return;
            }

           remainder = player.getInventory().addItem(loading);
        }
        else {
            ItemStack currentItem = gui.getItem(getInputSlot());
            if (currentItem == null) {
                init();
                return;
            }
               remainder = player.getInventory().addItem(currentItem);
        }

        if (remainder.isEmpty()) {
            init();
            return;
        }

        World world = player.getWorld();
        for (ItemStack item : remainder.values()) {
            world.dropItem(player.getLocation(), item);
        }

        init();

    }


    @Override
    protected boolean isStaticItem(ItemStack current) {
        boolean isLoad = LOAD.isSimilar(current) || BLOCK.isSimilar(current) || STICK.isSimilar(current);
        return isLoad || currentLoaded().isSimilar(current);
    }

    private ItemStack currentLoaded() {
        Artillery owner = (Artillery)getOwner();
        AmmoItem ammo = owner.getLoadedAmmoType();
        String load;
        if (ammo == null || owner.getAmmo() <= 0) {
            load = ChatColor.RED+"None";
        }
        else {
            load = ChatColor.GOLD + ammo.getName();
        }

        return StaticItem.INFO.toItem(ChatColor.WHITE+"Loaded ammo: "+load);
    }


    @Override
    public void updateState() {
        ItemStack stack = currentLoaded();
        gui.setItem(gui.getSize()-2, stack);

    }
}
