package me.camm.productions.fortressguns.Util.damage.builder;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DamageInfo {

    private DamageField field;
    private @Nullable Entity shooter;
    private Entity source;
    private float damage;

    public DamageInfo(DamageField field, @Nullable Entity shooter, Entity source, float damage) {
        this.field = field;
        this.shooter = shooter;
        this.source = source;
        this.damage = damage;
    }

    public DamageField getField() {
        return field;
    }

    public @Nullable Entity getShooter() {
        return shooter;
    }

    public Entity getSource() {
        return source;
    }

    public float getDamage() {
        return damage;
    }
}
