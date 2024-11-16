package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.LightFlak;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterLightFlak extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 15,
            SchemaKey.JAM_PERCENT.getLabel(),0f,
            SchemaKey.OVERHEAT.getLabel(),0f,
            SchemaKey.MAG_SIZE.getLabel(),0,
            SchemaKey.COOLDOWN.getLabel(), 1000
    );



    public AdapterLightFlak(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {
        String health = SchemaKey.HEALTH.getLabel();
        String jam = SchemaKey.JAM_PERCENT.getLabel();
        String overheat = SchemaKey.OVERHEAT.getLabel();
        String magSize = SchemaKey.MAG_SIZE.getLabel();
        String cooldown = SchemaKey.COOLDOWN.getLabel();

        if (values != null && values.containsKey(health)) {
            LightFlak.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        }
        else LightFlak.setMaxHealth((Integer)DEFAULTS.get(health));

        if (values != null && values.containsKey(jam)) {
            LightFlak.setJamPercent(validateJam(values.get(jam), (Float)DEFAULTS.get(jam)));
        }
        else LightFlak.setJamPercent((Float)DEFAULTS.get(jam));


        if (values != null && values.containsKey(overheat)) {
            LightFlak.setOverheat(validateHeat(values.get(overheat), (Float)DEFAULTS.get(overheat)));
        }
        else LightFlak.setOverheat((Float)DEFAULTS.get(overheat));


        if (values != null && values.containsKey(magSize)) {
            LightFlak.setMagSize(validateIntPositive(values.get(magSize), (Integer)DEFAULTS.get(magSize)));
        }
        else LightFlak.setMagSize((Integer)DEFAULTS.get(magSize));


        if (values != null && values.containsKey(cooldown)) {
            LightFlak.setCooldown(validateIntPositive(values.get(cooldown), (Integer)DEFAULTS.get(cooldown)));
        }
        else LightFlak.setCooldown((Integer)DEFAULTS.get(cooldown));

    }
}
