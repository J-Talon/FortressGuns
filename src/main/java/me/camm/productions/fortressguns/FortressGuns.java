package me.camm.productions.fortressguns;

import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Handlers.InventoryHandler;
import me.camm.productions.fortressguns.Handlers.ItemMergeHandler;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Serialization.FileManager;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.Util.command.CommandListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FortressGuns extends JavaPlugin {

    private static FortressGuns plugin;
    private MissileLockNotifier notifier;

    CommandListener commandHandler;
    private Logger logger;

    public static Plugin getInstance(){
      return plugin;
    }

    @Override
    public void onEnable() {
      plugin = this;
      FileManager.loadArtilleryConfig();

      InteractionHandler interactionHandler = InteractionHandler.getInstance();
      ChunkLoader loader = ChunkLoader.getInstance();

      this.logger = getLogger();

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
