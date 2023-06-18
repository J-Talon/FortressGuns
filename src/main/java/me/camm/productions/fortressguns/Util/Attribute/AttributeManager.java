package me.camm.productions.fortressguns.Util.Attribute;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AttributeManager {

    private static AttributeManager manager = null;

    private Map<Class<? extends Artillery>, Map<String, Object>> attributes;

    private AttributeManager(){
        attributes = new HashMap<>();
    }

    public static AttributeManager getInstance() {
        if (manager == null) {
            manager = new AttributeManager();
        }
        return manager;
    }

    public void addAttribute(Map<String, Object> entries, Class<? extends Artillery> clazz){
        attributes.put(clazz, entries);
    }


    public void setAttributesForClass(Artillery artillery){
        Class<? extends Artillery> arty = artillery.getClass();
        if (!attributes.containsKey(arty))
            return;

        for (String key: attributes.get(arty).keySet()) {
            try {
                /// have each arty have default values.
                Field field = arty.getDeclaredField(key);
                Class<?> t = field.getType();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
