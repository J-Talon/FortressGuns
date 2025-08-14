package me.camm.productions.fortressguns.Util.DataLoading.Config;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("projectiles")
public class ConfigArtilleryProjectiles implements ConfigObject {

    float solidShellWeight = 1.2F;

    float solidLightShellWeight = 0.1F;


    @Override
    public boolean apply() {
        if (!new ValidatorProj().validate(this)) {
            return false;
        }

        StandardHeavyShell.setWeight(solidShellWeight);
        StandardLightShell.setWeight(solidLightShellWeight);

        return true;
    }

    static class ValidatorProj implements Validator<ConfigArtilleryProjectiles> {
        @Override
        public boolean validate(@NotNull ConfigArtilleryProjectiles in) {
            return in.solidLightShellWeight >= 0 &&
                    in.solidShellWeight >= 0;
        }
    }
}
