package me.camm.productions.fortressguns.Util.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class CommandHandler {

    public abstract boolean execute(CommandSender sender, String[] args);

    public abstract List<String> getTabCompletes(CommandSender sender, String[] in);



}
