package me.camm.productions.fortressguns.Util.DataLoading.Schema.ConstructSchema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.LightArtillery;

@JsonTypeName("fieldLight")
public class ConfigLightArtillery extends ConfigArtilleryGeneral {
    @Override
    public boolean apply() {
        if (!super.apply())
            return false;

        LightArtillery.setCooldown(cooldown);
        LightArtillery.setMaxHealth(health);
        return true;
    }
}
