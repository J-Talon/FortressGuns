package me.camm.productions.fortressguns.Util.command;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

import static me.camm.productions.fortressguns.Util.command.PermissionNodeLabel.FG_DEBUG;

public class CommandCheckChunk extends CommandHandler {


    @Override
    public boolean execute(CommandSender sender, String[] args) {

        World world = findWorldOrDefault(sender);
        if (world == null) {
            sender.sendMessage("Error: No worlds loaded");
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        int[] arr = new int[args.length];
        for (int i = 0; i <args.length; i ++) {
            String elem = args[i];
            try {
                arr[i] = Integer.parseInt(elem);
            }
            catch (NumberFormatException e) {
                sender.sendMessage("Error parsing argument "+elem);
                return false;
            }
        }

        Chunk c;
        if (world.isChunkLoaded(arr[0], arr[1])) {
            c = world.getChunkAt(arr[0], arr[1]);
            sender.sendMessage("Chunk " +arr[0] +", "+arr[1] +" @ "+ world.getName() +" is loaded");
            sender.sendMessage("Entities loaded: "+c.isEntitiesLoaded());
            sender.sendMessage("Force loaded: "+c.isForceLoaded());
            sender.sendMessage("Inhabited time: "+c.getInhabitedTime());
            sender.sendMessage("FG ticket count : "+c.getPluginChunkTickets().size());
        }
        else {
            sender.sendMessage("Chunk " +arr[0] +", "+arr[1] +" @ "+ world.getName() +" is unloaded");
        }
        return true;
    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }


    @Override
    public String getPermissionNode() {
        return FG_DEBUG.label();
    }
}
