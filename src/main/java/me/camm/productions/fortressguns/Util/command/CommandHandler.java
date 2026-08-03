package me.camm.productions.fortressguns.Util.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CommandHandler {

    public abstract boolean execute(CommandSender sender, String[] args);

    public abstract List<String> getTabCompletes(CommandSender sender, String[] in);


    protected World findWorldOrDefault(CommandSender sender) {
        World world;
        if (! (sender instanceof Player)) {
            List<World> worlds = sender.getServer().getWorlds();
            if (worlds.isEmpty()) return null;

            world = worlds.get(0);
        }
        else world = ((Player) sender).getWorld();
        return world;
    }



}
