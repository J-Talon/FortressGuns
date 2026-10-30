package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

/*
[shellHE]
explosionPower = 4
explosionDamage = 25
damage = 10

 */
public class SectionShellHE extends ConfigSection {

    private Double explosionPower;
    private Double explosionDamage;
    private Double damage;

    public SectionShellHE(Double explosionPower, Double explosionDamage, Double damage) {
        this.explosionPower = explosionPower;
        this.explosionDamage = explosionDamage;
        this.damage = damage;
    }

    @Override
    public boolean isValid() {
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
