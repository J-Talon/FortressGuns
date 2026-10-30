package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Section;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCheckConfig extends CommandHandler {


    private List<String> sections;
    private List<String> params;



    public CommandCheckConfig() {
        sections = new ArrayList<>();
        params = new ArrayList<>();

        Arrays.stream(Section.values()).forEach(section -> sections.add(section.getSection()));
        Arrays.stream(ConfigField.values()).forEach(value -> params.add(value.toString()));


    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) {
            sender.sendMessage("Config didn't load properly, cannot retrieve value!");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (String s: args) {
            builder.append(s).append(".");
        }
        String value = manager.getRawValue(builder.toString());
        if (value == null) {
            sender.sendMessage("Could not retrieve value; Either value doesn't exist or error occurred");
            return true;
        }

        sender.sendMessage(value);
        return true;
    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        if (in.length == 1) {
            return sections;
        }

        if (in.length == 2) {
            return params;
        }
        return List.of();
    }


    @Override
    public String getPermissionNode() {
        return PermissionNodeLabel.FG_DEBUG.label();
    }
}
