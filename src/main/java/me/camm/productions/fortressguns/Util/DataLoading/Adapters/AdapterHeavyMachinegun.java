package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import com.google.common.collect.ImmutableMap;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyMachineGun;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;

import java.util.Map;

public class AdapterHeavyMachinegun extends AdapterArtillery {

    private static final ImmutableMap<String, Object> DEFAULTS = ImmutableMap.of(
            SchemaKey.HEALTH.getLabel(), 15,
            SchemaKey.JAM_PERCENT.getLabel(),0f,   //make sure you put the f if it's a float
            SchemaKey.OVERHEAT.getLabel(),0f,      //or else you'll need an f from chat
            SchemaKey.MAG_SIZE.getLabel(),0        //same thing with Ls if you need them
    );




    public AdapterHeavyMachinegun(Map<String, Object> values) {
        super(values);
    }

    @Override
    protected void setConfig() {
        String health = SchemaKey.HEALTH.getLabel();
        String jam = SchemaKey.JAM_PERCENT.getLabel();
        String overheat = SchemaKey.OVERHEAT.getLabel();
        String magSize = SchemaKey.MAG_SIZE.getLabel();

        if (values != null && values.containsKey(health)) {
            HeavyMachineGun.setMaxHealth(validateIntPositive(values.get(health), (Integer)DEFAULTS.get(health)));
        }
        else HeavyMachineGun.setMaxHealth((Integer)DEFAULTS.get(health));

        if (values != null && values.containsKey(jam)) {
            HeavyMachineGun.setJamPercent(validateJam(values.get(jam), (Float)DEFAULTS.get(jam)));
        }
        else HeavyMachineGun.setJamPercent((Float)DEFAULTS.get(jam));


        if (values != null && values.containsKey(overheat)) {
            HeavyMachineGun.setOverheat(validateHeat(values.get(overheat), (Float)DEFAULTS.get(overheat)));
        }
        else HeavyMachineGun.setOverheat((Float)DEFAULTS.get(overheat));


        if (values != null && values.containsKey(magSize)) {
            HeavyMachineGun.setMagSize(validateIntPositive(values.get(magSize), (Integer)DEFAULTS.get(magSize)));
        }
        else HeavyMachineGun.setMagSize((Integer)DEFAULTS.get(magSize));
    }
}
