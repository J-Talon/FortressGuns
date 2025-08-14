package me.camm.productions.fortressguns.Util.DataLoading.Config;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShellHE;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("explosions")
public class ConfigArtilleryExplosions implements ConfigObject {
    float heavyHighExplosive = 4;
    float heavyFlak = 4;
    float missile = 4;
    float standard = 4;


    @Override
    public boolean apply() {
        if (!new ValidatorExplosions().validate(this))
            return false;

        HeavyShellHE.setExplosionPower(heavyHighExplosive);
        FlakHeavyShell.setExplosionPower(heavyFlak);
        StandardHeavyShell.setExplosionPower(standard);
        SimpleMissile.setExplosionPower(missile);

        return true;
    }


    static class ValidatorExplosions implements Validator<ConfigArtilleryExplosions> {
        @Override
        public boolean validate(@NotNull ConfigArtilleryExplosions in) {
            return in.heavyHighExplosive >= 0 && in.heavyFlak >= 0 &&
                    in.missile >= 0 && in.standard >= 0;
        }
    }

    public float getHeavyHighExplosive() {
        return heavyHighExplosive;
    }

    public void setHeavyHighExplosive(float heavyHighExplosive) {
        this.heavyHighExplosive = heavyHighExplosive;
    }

    public float getHeavyFlak() {
        return heavyFlak;
    }

    public void setHeavyFlak(float heavyFlak) {
        this.heavyFlak = heavyFlak;
    }

    public float getMissile() {
        return missile;
    }

    public void setMissile(float missile) {
        this.missile = missile;
    }

    public float getStandard() {
        return standard;
    }

    public void setStandard(float standard) {
        this.standard = standard;
    }
}
