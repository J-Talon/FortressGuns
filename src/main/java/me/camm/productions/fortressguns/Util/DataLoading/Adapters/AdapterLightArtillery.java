package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.LightArtillery;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterLightArtillery extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 15,
            SchemaKey.COOLDOWN.getLabel(), 1000
    );

    public AdapterLightArtillery(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {
        String health = SchemaKey.HEALTH.getLabel();
        String cool = SchemaKey.COOLDOWN.getLabel();

        if (values != null && values.containsKey(health)) {
            LightArtillery.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        }
        else {
            System.out.println("Set default health 2:");
            LightArtillery.setMaxHealth((Integer)DEFAULTS.get(health));
        }
        
        
        if (values != null && values.containsKey(cool)) {
            LightArtillery.setCooldown(validateIntPositive(values.get(cool), (Integer)DEFAULTS.get(cool)));
        }
        else LightArtillery.setCooldown((Integer)DEFAULTS.get(cool));
    }
}
