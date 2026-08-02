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


enum CommandHeader {
    CHECK_TICKET("ct");

    private final String s;
    private CommandHeader(String s) {
        this.s = s;
    }

    public String str() {
        return s;
    }

}

enum PluginCommands {

    CT(CommandHeader.CHECK_TICKET, new CommandCheckTicket());

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
        if (!(handlers.containsKey(header)))
            return true;

        return handlers.get(header).execute(commandSender, strings);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CommandHandler hand = handlers.getOrDefault(s, null);
        if (hand == null)
            return List.of("Error: could not find handler");

        return hand.getTabCompletes(commandSender, strings);
    }
}
