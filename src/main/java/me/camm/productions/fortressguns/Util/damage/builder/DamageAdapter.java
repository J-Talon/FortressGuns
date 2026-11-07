package me.camm.productions.fortressguns.Util.damage.builder;

import me.camm.productions.fortressguns.Util.Adapter;
import me.camm.productions.fortressguns.Util.EntityUtils;
import me.camm.productions.fortressguns.Util.damage.provider.DamageProvider1_17_1;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DamageAdapter {

    private final Map<Class<?>, Adapter<Entity, DamageInfo>> damageRoutes;

    private static DamageAdapter provider = null;

    private DamageAdapter() {
        damageRoutes = new HashMap<>();

        addRoute(new DamageProvider1_17_1());  //todo inline into version provider
    }

    public static DamageAdapter getInstance() {
        if (provider == null) {
            provider = new DamageAdapter();
        }
        return provider;
    }


    public void addRoute(Adapter<Entity, DamageInfo> route) {
        damageRoutes.put(route.getTarget(), route);
    }

    public @Nullable Adapter<Entity, DamageInfo> getRoute(Class<?> target) {
        return damageRoutes.getOrDefault(target, null);
    }


    public boolean accept(Entity damagee, DamageInfo data) {

        Class<?> target = EntityUtils.mapToNMS(damagee);

        Adapter<Entity, DamageInfo> adapter = getRoute(target);
        if (adapter == null) return false;

        adapter.provide(damagee, data);
        return true;
    }



}
