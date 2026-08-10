package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.camm.productions.fortressguns.Util.command.PermissionNodeLabel.FG_DEBUG;


public class CommandCheckTicket extends CommandHandler {

    private @Nullable List<UUID> getTickets(CommandSender sender) {

        World w = findWorldOrDefault(sender);
        if (w == null) return null;
        return ChunkLoader.getInstance().getLoadingTickets(w.getName());
    }



    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Error: Specify the ticket UUID");
            return false;
        }

        World w = findWorldOrDefault(sender);
        if (w == null) {
            sender.sendMessage("Could not find world");
            return true;
        }

        sender.sendMessage(ChunkLoader.getInstance().checkTicket(w.getName(), args[0]));
        return true;
    }



    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] args) {

        if (args.length > 1)
            return List.of();

        List<UUID> ids = getTickets(sender);
        if (ids == null) {return List.of("Error: No worlds loaded");}

        List<String> out = new ArrayList<>();
        ids.forEach(id -> out.add(id.toString()));
        return out;
    }

    @Override
    public String getPermissionNode() {
        return FG_DEBUG.label();
    }
}
