package me.camm.productions.fortressguns;


import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemCreator;
import me.camm.productions.fortressguns.Handlers.ExplosionHandler;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
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
      PluginManager manager = getServer().getPluginManager();
      manager.registerEvents(new InteractionHandler(),this);
      manager.registerEvents(new ExplosionHandler(),this);
      manager.registerEvents(this, this);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Inventory inv = player.getInventory();

        for (ArtilleryType type: ArtilleryType.values()) {
            ItemStack created = ArtilleryItemCreator.createArtilleryItem(type);
            inv.addItem(created);
        }
    }

    @Override
    public void onDisable() {
        MissileLockNotifier.get(this).stop();
    }



}
