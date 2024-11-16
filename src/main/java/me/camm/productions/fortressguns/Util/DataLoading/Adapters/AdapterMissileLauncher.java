package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.MissileLauncher;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterMissileLauncher extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 50,
            SchemaKey.COOLDOWN.getLabel(),5000,
            SchemaKey.NUM_MISSILES.getLabel(), 6
    );



    public AdapterMissileLauncher(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {
        String health = SchemaKey.HEALTH.getLabel();
        String cooldown = SchemaKey.COOLDOWN.getLabel();
        String miss = SchemaKey.NUM_MISSILES.getLabel();

        if (values != null && values.containsKey(health)) {
            MissileLauncher.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        } else
            MissileLauncher.setMaxHealth((Integer)DEFAULTS.get(health));

        if (values != null && values.containsKey(cooldown)) {
            MissileLauncher.setCooldown(validateIntPositive(values.get(cooldown), (Integer)DEFAULTS.get(cooldown)));
        }
        else
            MissileLauncher.setCooldown((Long)DEFAULTS.get(cooldown));

        if (values != null && values.containsKey(miss)) {
            MissileLauncher.setMaxRockets(validateIntPositive(values.get(miss), (Integer)DEFAULTS.get(miss)));
        }
        else MissileLauncher.setMaxRockets((Integer)DEFAULTS.get(miss));



    }
}
