package me.camm.productions.fortressguns.Util.DataLoading.Config;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("general")
public class ConfigGeneral implements ConfigObject {


    boolean requireReloading = true;

    float missileDifficulty = 100;

    boolean enableFlares = true;

    boolean useVanillaExplosions = false;

    boolean destructiveExplosions = true;

    public boolean isRequireReloading() {
        return requireReloading;
    }

    public void setRequireReloading(boolean requireReloading) {
        this.requireReloading = requireReloading;
    }

    public float getMissileDifficulty() {
        return missileDifficulty;
    }

    public void setMissileDifficulty(float missileDifficulty) {
        this.missileDifficulty = missileDifficulty;
    }

    public boolean isEnableFlares() {
        return enableFlares;
    }

    public void setEnableFlares(boolean enableFlares) {
        this.enableFlares = enableFlares;
    }

    public boolean isUseVanillaExplosions() {
        return useVanillaExplosions;
    }

    public void setUseVanillaExplosions(boolean useVanillaExplosions) {
        this.useVanillaExplosions = useVanillaExplosions;
    }

    public boolean isDestructiveExplosions() {
        return destructiveExplosions;
    }

    public void setDestructiveExplosions(boolean destructiveExplosions) {
        this.destructiveExplosions = destructiveExplosions;
    }

    @Override
    public boolean apply() {
        if (!new ValidatorGeneral().validate(this)) {
            return false;
        }

        Artillery.setRequiresReloading(requireReloading);
        SimpleMissile.setDifficulty(missileDifficulty);
        ExplosionFactory.setUseVanillaExplosions(useVanillaExplosions);
        ExplosionFactory.setDestructiveExplosions(destructiveExplosions);
        return true;
    }


    static class ValidatorGeneral implements Validator<ConfigGeneral> {
        @Override
        public boolean validate(@NotNull ConfigGeneral in) {
            return in.missileDifficulty >= 0 && in.missileDifficulty <= 100;
        }
    }
}
