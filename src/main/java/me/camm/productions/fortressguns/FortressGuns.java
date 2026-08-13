package me.camm.productions.fortressguns;


import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.item.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.item.ArtilleryItems.ItemUtils;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Handlers.InventoryHandler;
import me.camm.productions.fortressguns.Handlers.ItemMergeHandler;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import me.camm.productions.fortressguns.Util.Serialization.FileManager;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.Util.command.CommandListener;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.camm.productions.fortressguns.item.ArtilleryItems.FlareGun;

public final class FortressGuns extends JavaPlugin {

    private static FortressGuns plugin;
    private InteractionHandler interactionHandler;
    private ChunkLoader loader;

    CommandListener commandHandler;
    private Logger logger;

    public static Plugin getInstance(){
      return plugin;
    }

    @Override
    public void onEnable() {
      plugin = this;
      FileManager.loadArtilleryConfig();

      interactionHandler = InteractionHandler.getInstance();
      loader = ChunkLoader.getInstance();

      this.logger = getLogger();

      PluginManager manager = getServer().getPluginManager();
      manager.registerEvents(interactionHandler,this);
      manager.registerEvents(new InventoryHandler(), this);
      manager.registerEvents(ItemMergeHandler.getInstance(),this);
      manager.registerEvents(loader, plugin);
      manager.registerEvents(new FlareGun(), this);

      commandHandler = new CommandListener();

    }


    @Override
    public void onDisable() {
        logger.log(Level.INFO,"Shutting down...");
        MissileLockNotifier.get(this).stop();

        logger.info("Unloading active pieces...");
        ChunkLoader.getActivePieces().forEach(construct -> {
            if (!construct.isInvalid()) {
                logger.info("Unloading construct: "+construct);
                construct.unload();
            }
        });
    }



}
