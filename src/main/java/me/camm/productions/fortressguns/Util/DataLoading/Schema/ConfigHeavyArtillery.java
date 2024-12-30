package me.camm.productions.fortressguns.Util.DataLoading.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyArtillery;

@JsonTypeName("fieldHeavy")
public class ConfigHeavyArtillery extends ConfigArtilleryGeneral {

    @Override
    public boolean apply() {
        if (!super.apply())
                return false;

        HeavyArtillery.setFireCooldown(cooldown);
        HeavyArtillery.setMaxHealth(health);
        return true;
    }
}
