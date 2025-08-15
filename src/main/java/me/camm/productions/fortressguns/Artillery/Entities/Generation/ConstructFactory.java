package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class ConstructFactory<T extends Construct> {

    static Map<Integer, ConstructType> constructsForward;
    static Map<ConstructType, Integer> constructsBackward;

    static Map<Integer, AmmoItem> itemsForward;
    static Map<AmmoItem, Integer> itemsBackward;

    static final String KEY = "construct";

    /*
    if you do make this version independent you have 2 choices:
    - either make a factory for each version or << preferable
    - make each function work out the version and return the correct version type
     */

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

    public static EulerAngle deserializeRotation(int...params) {
        return new EulerAngle(deserializeRotation(params[1]),deserializeRotation(params[2]),deserializeRotation(params[3]));
    }

    protected void deserializeSetAmmo(T construct, int ... params){
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

    public abstract @Nullable T create(Location loc, int ... params);


    static class FactoryHeavyArtillery extends ConstructFactory<HeavyArtillery> {

        public HeavyArtillery create(Location loc, int... params) {

            if (params.length < 4)
                return null;

            HeavyArtillery artillery = new HeavyArtillery(loc, loc.getWorld(),deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }


    static class FactoryHeavyFlak extends ConstructFactory<HeavyFlak> {

        public HeavyFlak create(Location loc, int... params) {
            if (params.length < 4)
                return null;

            HeavyFlak artillery = new HeavyFlak(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }

    static class FactoryHMG extends ConstructFactory<HeavyMachineGun> {
        public HeavyMachineGun create(Location loc, int... params) {
            if (params.length < 4) {
                return null;
            }

            HeavyMachineGun artillery = new HeavyMachineGun(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }

    static class FactoryLightFlak extends ConstructFactory<LightFlak> {
        public LightFlak create(Location loc, int... params) {
            if (params.length < 4) {
                return null;
            }

            LightFlak artillery = new LightFlak(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }

    static class FactoryMissileLauncher extends ConstructFactory<MissileLauncher> {
        public MissileLauncher create(Location loc, int... params) {
            if (params.length < 4) {
                return null;
            }

            MissileLauncher artillery = new MissileLauncher(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }

    static class FactoryLightArtillery extends ConstructFactory<LightArtillery> {
        public LightArtillery create(Location loc, int... params) {
            if (params.length < 4) {
                return null;
            }

            LightArtillery artillery = new LightArtillery(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            return artillery;
        }
    }



}
