package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyFlak;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterHeavyFlak extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 80,
            SchemaKey.COOLDOWN.getLabel(), 5000
    );

    public AdapterHeavyFlak(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {
        String health = SchemaKey.HEALTH.getLabel();
        String cooldown = SchemaKey.COOLDOWN.getLabel();

        if (values != null && values.containsKey(health)) {
            HeavyFlak.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        }
        else HeavyFlak.setMaxHealth((Integer)DEFAULTS.get(health));


        if (values != null && values.containsKey(cooldown)) {
            long value = validateIntPositive(values.get(cooldown), (Integer)DEFAULTS.get(cooldown));
            HeavyFlak.setFireCooldown(value);
        }
        else HeavyFlak.setCooldown((Integer)DEFAULTS.get(cooldown));

    }
}
