package me.camm.productions.fortressguns.Core.Artillery.Property;

import me.camm.productions.fortressguns.Core.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Operator2;
import me.camm.productions.fortressguns.Util.DataHelper;
import org.bukkit.ChatColor;

public enum ItemPropertyField {
    HEALTH(ChatColor.GRAY+"Health",
            (o, s) -> {
        double health = DataHelper.stringToDouble(s, -1);
        if (health < 0) return;
        o.setHealth(health);
    }),


    ;

    private final String property;
    private final Operator2<Construct, String> deserializer;

    ItemPropertyField(String property, Operator2<Construct, String> deserializer) {
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
