package me.camm.productions.fortressguns.Util.Serialization;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Operator;
import me.camm.productions.fortressguns.Util.String.StringHelper;
import org.bukkit.ChatColor;

import java.util.List;

public enum ItemPropertyField {
    HEALTH(ChatColor.GRAY+"Health",
            (o, s) -> {
        double health = StringHelper.stringToDouble(s, -1);
        if (health < 0) return;
        o.setHealth(health);
    }),


    ;

    private final String property;
    private final Operator<Construct, String> deserializer;

    ItemPropertyField(String property, Operator<Construct, String> deserializer) {
        this.deserializer = deserializer;
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public String build(String in) {
        return property + ":" + in;
    }

    public void performOperation(Construct c, String context) {
        deserializer.accept(c, context);
    }
}
