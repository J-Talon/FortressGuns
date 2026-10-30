package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
[solidShell]
weight = 1.2
damage = 25
 */
public class SectionShellSolid extends ConfigSection {

    private double weight;
    private double damage;


    public SectionShellSolid(double weight, double damage) {
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
