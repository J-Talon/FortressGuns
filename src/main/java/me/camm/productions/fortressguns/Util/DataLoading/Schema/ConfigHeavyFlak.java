package me.camm.productions.fortressguns.Util.DataLoading.Schema;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyFlak;

@JsonTypeName("heavyFlak")
public class ConfigHeavyFlak extends ConfigArtilleryGeneral {

    @Override
    public boolean apply() {
        if (!super.apply())
            return false;

        HeavyFlak.setMaxHealth(health);
        HeavyFlak.setCooldown(cooldown);
        return true;
    }
}
