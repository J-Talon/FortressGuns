package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
[HMGBullet]
weight = 0.1
damage = 5

 */
public class SectionHMGBullet extends ConfigSection {

    private double weight;
    private double damage;

    public SectionHMGBullet(double weight, double damage) {
        this.weight = weight;
        this.damage = damage;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    public double getWeight() {
        return weight;
    }

    public double getDamage() {
        return damage;
    }
}
