package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.builder.ConfigHelper;

/*
[solidShell]
weight = 1.2
damage = 25
 */
public class SectionShellSolid extends ConfigSection {

    private Double weight;
    private Double damage;


    public SectionShellSolid(Double weight, Double damage) {
        this.weight = weight;
        this.damage = damage;
    }

    @Override
    public boolean isValid() {
        try {
            ConfigHelper.checkProjWD(weight,damage);
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
