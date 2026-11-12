package me.camm.productions.fortressguns.Util.damage.builder;

import org.bukkit.entity.Entity;

public enum DamageField {

    BULLET("bullet", "was shot to bits"),

    ARTILLERY("artillery_direct", "was turned into meat confetti");

    private final String field;
    private final String message;

    private DamageField(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
