package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FactorySerialization {


    static Map<Integer, ConstructType> constructsForward;
    static Map<ConstructType, Integer> constructsBackward;

    static Map<Integer, AmmoItem> itemsForward;
    static Map<AmmoItem, Integer> itemsBackward;

    static final String KEY = "construct";


    static {
        constructsForward = new HashMap<>();
        constructsBackward = new HashMap<>();
        for (ConstructType type: ConstructType.values()) {
            constructsForward.put(type.ordinal(), type);
            constructsBackward.put(type, type.ordinal());
        }

        itemsForward = new HashMap<>();
        itemsBackward = new HashMap<>();
        for (AmmoItem item: AmmoItem.values()) {
            itemsForward.put(item.ordinal(),item);
            itemsBackward.put(item, item.ordinal());
        }
    }



    public static @Nullable ConstructType deserializeType(int value) {
        return constructsForward.getOrDefault(value, null);
    }

    public static int serializeType(Construct cons) {
        return constructsBackward.getOrDefault(cons.getType(), -1);
    }

    public static @Nullable AmmoItem deserializeAmmo(int value) {
        return itemsForward.getOrDefault(value, null);
    }

    public static int serializeAmmo(AmmoItem item) {
        return itemsBackward.getOrDefault(item, -1);
    }

    public static double deserializeRotation(int value) {
        return ((double)value) / 100;
    }

    public static int serializeRotation(double value) {
        return (int)(value * 100);
    }

    public static Integer[] serializeRotation(EulerAngle angle) {
        return new Integer[]{serializeRotation(angle.getX()),serializeRotation(angle.getY()),serializeRotation(angle.getZ())};
    }


    //below functions assume that params is everything from the type to ammo and beyond.
    //[type, rotation x, rotation z, rotation y, ammotype, ammo,...]
    public static EulerAngle deserializeRotation(int...params) {
        return new EulerAngle(deserializeRotation(params[1]),deserializeRotation(params[2]),deserializeRotation(params[3]));
    }


    protected static void deserializeSetAmmo(Construct construct, int ... params){
        if (!(construct instanceof Artillery artillery))
            throw new IllegalArgumentException("deserializing construct must be subclass of artillery:"+construct.getClass());

        if (params.length >= 6) {
            AmmoItem item = deserializeAmmo(params[4]);
            int amount = params[5];
            artillery.setAmmo(amount);
            artillery.setLoadedAmmoType(item);
        }
    }

    public static String getKey() {
        return KEY;
    }
}
