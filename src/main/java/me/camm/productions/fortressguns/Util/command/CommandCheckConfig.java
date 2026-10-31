package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.builder.ConfigField;
import me.camm.productions.fortressguns.Util.configv2.builder.Section;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CommandCheckConfig extends CommandHandler {


    private final List<String> sections;
    private final List<String> params;



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

        Predicate<String> pred = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return sections.contains(s) || params.contains(s);
            }
        };


        boolean allMatch = Arrays.stream(args).allMatch(pred);
        if (!allMatch) {
            sender.sendMessage("No config exists that conforms to parameters");
            return false;
        }




        StringBuilder builder = new StringBuilder();
        for (int s = 0; s < args.length; s ++) {
            builder.append(args[s]);
            if (s >= args.length - 1) break;
            builder.append(".");
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
