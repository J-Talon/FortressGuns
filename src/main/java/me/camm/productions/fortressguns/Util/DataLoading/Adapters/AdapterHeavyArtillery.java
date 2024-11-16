package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyArtillery;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterHeavyArtillery extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 50,
            SchemaKey.COOLDOWN.getLabel(),5000
    );



    public AdapterHeavyArtillery(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {

        String health = SchemaKey.HEALTH.getLabel();
        String cooldown = SchemaKey.COOLDOWN.getLabel();

        if (values != null && values.containsKey(health)) {
            HeavyArtillery.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        } else
            HeavyArtillery.setMaxHealth((Integer)DEFAULTS.get(health));

        if (values != null && values.containsKey(cooldown)) {
            HeavyArtillery.setFireCooldown(validateIntPositive(values.get(cooldown), (Integer)DEFAULTS.get(cooldown)));
        }
        else
            HeavyArtillery.setFireCooldown((Integer)DEFAULTS.get(cooldown));

    }
}
