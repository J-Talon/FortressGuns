package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

import java.util.logging.Logger;

/*
[HMGBullet]
weight = 0.1
damage = 5

 */
public class SectionHMGBullet extends ConfigSection {

    private Double weight;
    private Double damage;

    public SectionHMGBullet(Double weight, Double damage, String key) {
        super(key);
        this.weight = weight;
        this.damage = damage;
    }

    @Override
    public boolean isValid(Logger logger) {
        try {
            ConfigHelper.checkProjWD(weight, damage);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getDamage() {
        return damage;
    }
}
