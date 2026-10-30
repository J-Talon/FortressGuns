package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
[shellFlak]
explosionPower = 4
explosionDamage = 30
damage = 5
 */
public class SectionFlak extends ConfigSection {

    private double explosionPower;
    private double explosionDamage;
    private double damage;

    public SectionFlak(double explosionPower, double explosionDamage, double damage) {
        this.explosionPower = explosionPower;
        this.explosionDamage = explosionDamage;
        this.damage = damage;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    public double getExplosionPower() {
        return explosionPower;
    }

    public double getExplosionDamage() {
        return explosionDamage;
    }

    public double getDamage() {
        return damage;
    }
}
