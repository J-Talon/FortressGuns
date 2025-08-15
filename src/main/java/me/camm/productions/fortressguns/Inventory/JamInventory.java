package me.camm.productions.fortressguns.Inventory;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Inventory.Abstract.PanelInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.StaticItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class JamInventory extends PanelInventory {

    static ItemStack border;
    static ItemStack slider;
    static ItemStack round;


    private boolean inAnimation;

    static {
        border = StaticItem.BORDER.toItemRaw();
        slider = StaticItem.CLEAR_JAM.toItem(ChatColor.GRAY+"Click to clear jam");
        round = StaticItem.JAMMED_ROUND.toItemRaw();
    }




    public JamInventory(RapidFire owner, InventoryGroup group) {
        super(owner, InventoryCategory.JAM_CLEAR, group);
        functions.put(StaticItem.CLEAR_JAM.getName(), new ClearFunction());
        init();
    }

    @Override
    protected boolean isStaticItem(ItemStack current) {
        return border.isSimilar(current) || slider.isSimilar(current) || round.isSimilar(current);
    }

    @Override
    public void init() {
        inAnimation = false;

        int i;
        for (i = 0; i < 9; i ++) {
            gui.setItem(i, border);
        }

        for (i = 12; i < 15; i ++) {
            gui.setItem(i, slider);
        }

        for (i = 15; i < 18; i ++) {
            gui.setItem(i, round);
        }

        for (i = 18; i < 27; i ++) {
            gui.setItem(i, border);
        }
    }



    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        init();
    }


    private void startAnimation(Player player) {
        final ItemStack AIR = new ItemStack(Material.AIR);
        final int START = 6, END = 8;

        RapidFire body = (RapidFire)owner;
        World world = ((RapidFire) owner).getWorld();

        BukkitRunnable phase2 = new BukkitRunnable() {
            ItemStack last = AIR;
            int x = 0;
            boolean played = false;
            @Override
            public void run() {
                if (gui.getViewers().isEmpty()) {
                    init();
                    cancel();
                    return;
                }

                int rowLast = Math.abs(-x+1) * 9;

                if (x >= gui.getSize() / 9) {
                    for (int i = rowLast + START; i <= rowLast + END; i ++) {
                        gui.setItem(i,AIR);
                    }

                    if (body.requiresReloading()) {
                        body.setAmmo(Math.max(0, body.getAmmo() - 1));
                        ItemStack ammoDrop = ConstructItemHelper.createAmmoItem(body.getLoadedAmmoType());
                        world.dropItem(body.getLoc(), ammoDrop);
                    }

                    body.setJammed(false);
                    init();
                    cancel();
                    return;
                }
                int row = Math.abs(-x) * 9;
                ItemStack nextLast = last;
                for (int i = row + START; i <= row + END; i ++) {
                    nextLast = gui.getItem(i);
                    gui.setItem(i, round);
                }


                for (int i = rowLast + START; i <= rowLast + END; i ++) {
                    gui.setItem(i, last);
                }

                if (!played) {
                    played = true;
                    player.playSound(player.getLocation(), Sound.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1, 2);
                }

                last = nextLast;
                x ++;
            }
        };




        BukkitRunnable phase1 =
        new BukkitRunnable() {
            final int TUBE_START = 9;
            int start, end;
            int x = 0;
            @Override
            public void run() {
                if (gui.getViewers().isEmpty()) {
                    init();
                    cancel();
                    return;
                }

                if (x > 6) {
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE,SoundCategory.BLOCKS,1,2);
                    init();
                    body.setJammed(false);
                    cancel();
                    return;
                }
                else {
                    start = Math.abs(-x + 3) + TUBE_START;
                    end = start + 2;
                }

                gui.setItem(start, slider);
                gui.setItem(end, slider);

                if (x > 3) {
                    gui.setItem(start - 1, AIR);

                    if (body.getAmmo() - 1 > 0) {
                        for (int i = end + 1, iters = x - 3; iters > 0; iters--, i++) {
                            gui.setItem(i, round);
                        }
                    }
                }
                else {
                    gui.setItem(end + 1, AIR);
                }

                x ++;

            }
        };

        phase1.runTaskTimer(FortressGuns.getInstance(),0, 2);
        phase2.runTaskTimer(FortressGuns.getInstance(),5, 4);

    }



    class ClearFunction implements MenuFunction {

        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {

            if (inAnimation)
                return;

            RapidFire fire = (RapidFire) body;
            if (!fire.isJammed())
                return;

            ItemStack clicked = event.getCurrentItem();
            if (!preconditions(clicked))
                return;


            startAnimation((Player)event.getWhoClicked());

        }
    }
}


