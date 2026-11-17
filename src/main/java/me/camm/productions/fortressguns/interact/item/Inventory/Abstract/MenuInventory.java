package me.camm.productions.fortressguns.interact.item.Inventory.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.config.ConfigManager;
import me.camm.productions.fortressguns.Util.config.ConfigurationHolder;
import me.camm.productions.fortressguns.Util.config.Wrapper.Etc.SectionGeneral;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;


public abstract class MenuInventory extends PanelInventory implements ConfigurationHolder<SectionGeneral> {

    private static boolean configApplied = false;
    private static boolean easyRepairs = true;
    private static double repairAmount = 10;

    public MenuInventory(Construct owner, InventoryGroup group) {
        super(owner,InventoryCategory.MENU, group);

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        setStaticDefaults(manager.getGeneral());

    }

    @Override
    public void setStaticDefaults(@Nullable SectionGeneral data) {
        configApplied = true;
        if (data == null) return;
        easyRepairs = data.getEasyRepairs();
        repairAmount = data.getRepairAmount();
    }



    protected static MenuFunction openReloading = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {

            Player player = (Player)event.getWhoClicked();
            Artillery art = (Artillery) body;

            art.getInventoryGroup().openInventory(InventoryCategory.RELOADING,player);
        }
    };


   protected static MenuFunction disassemble = new MenuFunction() {
        @Override
        public void onEvent(InventoryClickEvent event, Construct body) {
            body.destroy(!body.isInvalid() && body.chunkLoaded(), false);
        }
    };




   protected static MenuFunction repair = new MenuFunction() {


       private void repairFail(Player player, String cause) {
           player.sendMessage(ChatColor.GOLD+"Could not repair artillery, " + cause);
       }

       @Override
       public void onEvent(InventoryClickEvent event, Construct body) {
           double health = body.getHealth();
           double maxHealth = body.getMaxHealth();
           Player player = (Player)event.getWhoClicked();

           if (health >= maxHealth) {
               repairFail(player, "already at full hp!");
               return;
           }


           Inventory playerInv = player.getInventory();
           int slot = playerInv.first(Material.IRON_INGOT);
           if (slot < 0) {
               repairFail(player, "no resources to repair!");
               return;
           }

           ItemStack stack = playerInv.getItem(slot);
           if (stack == null) {
               repairFail(player, "no resources to repair!");
               return;
           }

           int amount = stack.getAmount();
           if (amount > 1)
               stack.setAmount(amount - 1);
           else
               stack.setType(Material.AIR);
           playerInv.setItem(slot, stack);

           health = Math.min(maxHealth, health + repairAmount);
           body.setHealth(health);
           double prettyPrint = (double)((int)(health * 10)) / 10;

           World world = player.getWorld();
           world.spawnParticle(Particle.CRIT, body.getCoreEntity().getLocation(),20);

           player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS,1,1);
           player.sendMessage(ChatColor.GOLD+"Repaired construct, now at: "+prettyPrint+" hp");
       }
   };



}




