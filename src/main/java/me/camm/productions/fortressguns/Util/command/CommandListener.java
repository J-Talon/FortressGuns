package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


enum PermissionNodeLabel {
    FG_DEBUG("fg.debug"),
    FG_ADMIN("fg.admin");

    private final String label;

    private PermissionNodeLabel(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}


enum CommandHeader {
    CHECK_TICKET("ct"),
    CHUNK_STATUS("cs"),
    INSPECT_ENTITY("fginspect"),
    GET_ITEMS("fgitems");

    private final String s;
    private CommandHeader(String s) {
        this.s = s;
    }

    public String str() {
        return s;
    }

}



enum PluginCommands {

    CT(CommandHeader.CHECK_TICKET, new CommandCheckTicket()),
    CS(CommandHeader.CHUNK_STATUS, new CommandCheckChunk()),
    FG_INSPECT(CommandHeader.INSPECT_ENTITY, new CommandFGInspect()),
    FG_ITEMS(CommandHeader.GET_ITEMS, new CommandGiveItems());

    private final CommandHeader head;
    private final CommandHandler hand;
    private PluginCommands(CommandHeader header, CommandHandler handler) {
        this.head = header;
        this.hand = handler;
    }

    public CommandHandler getHand() {
        return hand;
    }

    public CommandHeader getHead() {
        return head;
    }
}





public class CommandListener implements CommandExecutor, TabCompleter {

    private final Map<String, CommandHandler> handlers;

    public CommandListener() {
        JavaPlugin p = (JavaPlugin) FortressGuns.getInstance();
        Logger logger = p.getLogger();
        handlers = new HashMap<>();

        for (PluginCommands s: PluginCommands.values()) {

            String head = s.getHead().str();
            handlers.put(s.getHead().str(),s.getHand());
            PluginCommand command = p.getCommand(head);
            if (command == null) {
                logger.log(Level.WARNING, "Could not register command "+ head);
                continue;
            }
            command.setExecutor(this);
            command.setTabCompleter(this);
        }

    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String header, @NotNull String[] strings) {
        if (!(handlers.containsKey(header))) {
            commandSender.sendMessage("Error: Could not find command handler");
            return true;
        }

        CommandHandler handler = handlers.get(header);
        String permission = command.getPermission();
        if (permission == null) permission = handler.getPermissionNode();

        if (!commandSender.hasPermission(permission)) {

            String permMessage = command.getPermissionMessage();
            if (permMessage == null)
                permMessage = "You do not have permission to use this command";

            commandSender.sendMessage(permMessage);
            return true;
        }

        return handler.execute(commandSender, strings);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandHandler hand = handlers.getOrDefault(s, null);
        if (hand == null)
            return List.of("Error: could not find handler");

        return hand.getTabCompletes(commandSender, strings);
    }
}
