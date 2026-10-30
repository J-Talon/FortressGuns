package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

import java.util.logging.Logger;

/*
[shellFlak]
explosionPower = 4
explosionDamage = 30
damage = 5
 */
public class SectionFlak extends ConfigSection {

    private Double explosionPower;
    private Double explosionDamage;
    private Double damage;

    public SectionFlak(Double explosionPower, Double explosionDamage, Double damage, String key) {
        super(key);
        this.explosionPower = explosionPower;
        this.explosionDamage = explosionDamage;
        this.damage = damage;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            ConfigHelper.checkProjExplodes3(explosionPower, explosionDamage, damage);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Double getExplosionPower() {
        return explosionPower;
    }

    public Double getExplosionDamage() {
        return explosionDamage;
    }

    public Double getDamage() {
        return damage;
    }
}
