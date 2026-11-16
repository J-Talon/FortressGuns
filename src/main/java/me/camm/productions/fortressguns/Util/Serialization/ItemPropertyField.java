package me.camm.productions.fortressguns.Util.Serialization;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Operator;
import me.camm.productions.fortressguns.Util.String.StringHelper;

public enum ItemPropertyField {
    HEALTH("Health", (o, s) -> {
        double health = StringHelper.stringToDouble(s, -1);
        if (health < 0) return;
        o.setHealth(health);
    }),


    ;

    private final String property;
    private final Operator<Construct, String> operator;

    ItemPropertyField(String property, Operator<Construct, String> operator) {
        this.operator = operator;
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public String build(String in) {
        return property + ":" + in;
    }

    public void performOperation(Construct c, String context) {
        operator.accept(c, context);
    }
}
