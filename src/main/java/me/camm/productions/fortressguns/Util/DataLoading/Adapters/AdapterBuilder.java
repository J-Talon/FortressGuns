package me.camm.productions.fortressguns.Util.DataLoading.Adapters;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;

public class AdapterBuilder {

    public static AdapterArtillery build(@Nullable Class<? extends AdapterArtillery> clazz, Map<String, Object> values) {

        if (clazz == null) {
            return null;
        }

     try {
         Constructor<? extends AdapterArtillery> cons = clazz.getConstructor(Map.class);
         return cons.newInstance(values);
     }
     catch (Exception e) {
         e.printStackTrace();
         return null;
     }
    }
}
