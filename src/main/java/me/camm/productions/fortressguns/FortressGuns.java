package me.camm.productions.fortressguns;


import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.ArtilleryItems.ConstructItemHelper;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Handlers.InventoryHandler;
import me.camm.productions.fortressguns.Handlers.ItemMergeHandler;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import me.camm.productions.fortressguns.Util.DataLoading.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FortressGuns extends JavaPlugin implements Listener {

    private static FortressGuns plugin;

    public static Plugin getInstance(){
      return plugin;
    }

    @Override
    public void onEnable() {
      plugin = this;
      FileManager.loadArtilleryConfig();

      PluginManager manager = getServer().getPluginManager();
      manager.registerEvents(new InteractionHandler(),this);
      manager.registerEvents(new InventoryHandler(), this);
      manager.registerEvents(this, this);
      manager.registerEvents(ItemMergeHandler.getInstance(),this);

    }

    ///temporary
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Inventory inv = player.getInventory();

        for (ConstructType type: ConstructType.values()) {
            ItemStack created = ConstructItemHelper.createArtilleryItem(type);
            inv.addItem(created);
        }

        for (AmmoItem item: AmmoItem.values()) {
            ItemStack ammo = ConstructItemHelper.createAmmoItem(item);
            inv.addItem(ammo);
        }

        inv.addItem(ConstructItemHelper.getStick());
    }

    @Override
    public void onDisable() {
        MissileLockNotifier.get(this).stop();
    }



}
