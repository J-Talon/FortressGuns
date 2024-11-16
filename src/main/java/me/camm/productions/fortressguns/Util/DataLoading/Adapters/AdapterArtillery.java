package me.camm.productions.fortressguns.Util.DataLoading.Adapters;


import me.camm.productions.fortressguns.Util.DataLoading.Validator.Validator;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class AdapterArtillery {

    protected Map<String, Object> values;

    protected int validateIntPositive(Object value, int defaultValue) {
        Integer res = new Validator.ValidateIntPositive().validate(value);

        if (res == null)
            return defaultValue;
        else return res;
    }

    protected float validateJam(Object value, float defaultValue) {

        Float res = new Validator.ValidateJam().validate(value);

        if (res == null)
            return defaultValue;


         return res;
    }

    protected float validateHeat(Object value, float defaultValue) {
        Float res = new Validator.ValidateHeat().validate(value);
        if (res == null)
            return defaultValue;
        else return res;
    }


    public AdapterArtillery(@Nullable Map<String, Object> values) {
        this.values = values;
        setConfig();
    }


    protected abstract void setConfig();


}
