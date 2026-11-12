package me.camm.productions.fortressguns;

import me.camm.productions.fortressguns.Handlers.ItemInteractionHandler;
import me.camm.productions.fortressguns.Handlers.InventoryHandler;
import me.camm.productions.fortressguns.Handlers.ItemMergeHandler;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Version;
import me.camm.productions.fortressguns.Util.config.FileManager;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.Util.command.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FortressGuns extends JavaPlugin {

    private static FortressGuns plugin;
    private static Version versionCode = null;


    private MissileLockNotifier notifier;

    CommandListener commandHandler;
    private Logger logger;

    public static Plugin getInstance(){
      return plugin;
    }

    public static Version getVersionCode() {
        return versionCode;
    }

    //todo create dedicated version manager
    private void mapToVersion() {

        Map<String, Version> versions = new HashMap<>();
        for (Version v: Version.values()) {
            versions.put(v.getVersionCode(), v);
        }

        String rawVersion = getServer().getClass().getPackage().getName();
        String code = rawVersion.substring(rawVersion.lastIndexOf(".") + 1);
        if (versions.containsKey(code)) {
            versionCode = versions.get(code);
        }
    }


    @Override
    public void onEnable() {
      plugin = this;
      this.logger = getLogger();

      mapToVersion();

      if (versionCode == null) {
          logger.log(Level.FINE,"Could not resolve supported version, shutting down");
          Bukkit.getPluginManager().disablePlugin(this);
      }
      else {
          logger.log(Level.INFO, "Loading plugin for version "+versionCode.getVersionCode() +"!");
      }

      FileManager.loadArtilleryConfig();

      ItemInteractionHandler interactionHandler = ItemInteractionHandler.getInstance();
      ChunkLoader loader = ChunkLoader.getInstance();

      PluginManager manager = getServer().getPluginManager();
      manager.registerEvents(interactionHandler,this);
      manager.registerEvents(new InventoryHandler(), this);
      manager.registerEvents(ItemMergeHandler.getInstance(),this);
      manager.registerEvents(loader, plugin);

      commandHandler = new CommandListener();
      notifier = MissileLockNotifier.get(this);

      RecipeManager.registerRecipes();
    }


    @Override
    public void onDisable() {
        logger.log(Level.INFO,"Shutting down...");
        notifier.stop();

        logger.info("Unloading active pieces...");
        ChunkLoader.getActivePieces().forEach(construct -> {
            if (!construct.isInvalid()) {
                logger.info("Unloading construct: "+construct);
                construct.unload();
            }
        });
    }



}
