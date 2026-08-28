package me.camm.productions.fortressguns.Util.DamageSource;

public enum DamageMultiplier {

    EXPLOSION(2f),
    FIRE(1.5f),
    GUN(1.2f),
    MAGIC(0.01f),
    DEFAULT(0.3f);

    public final float multiplier;

    DamageMultiplier(float mult){
        this.multiplier = mult;
    }


}
