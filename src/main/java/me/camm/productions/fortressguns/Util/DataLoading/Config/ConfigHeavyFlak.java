package me.camm.productions.fortressguns.Util.DataLoading.Config;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyFlak;

@JsonTypeName("heavyFlak")
public class ConfigHeavyFlak extends ConfigArtilleryCommon {

    @Override
    public boolean apply() {
        if (!super.apply())
            return false;

        HeavyFlak.setMaxHealth(health);
        HeavyFlak.setCooldown(cooldown);
        return true;
    }
}
