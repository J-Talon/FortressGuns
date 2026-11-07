package me.camm.productions.fortressguns.Util.damage.builder;

import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Adapter;
import me.camm.productions.fortressguns.Util.EntityUtils;
import me.camm.productions.fortressguns.Util.Version;
import me.camm.productions.fortressguns.Util.damage.provider.DamageProvider1_17_1;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DamageAdapter {

    private final Map<Version, Adapter<Entity, DamageInfo>> damageRoutes;

    private static Version code = null;
    private static DamageAdapter provider = null;

    private DamageAdapter() {
        damageRoutes = new HashMap<>();
        code = FortressGuns.getVersionCode();

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

    public @Nullable Adapter<Entity, DamageInfo> getRoute(Version version) {
        return damageRoutes.getOrDefault(version, null);
    }


    public boolean accept(Entity damagee, DamageInfo data) {

        Adapter<Entity, DamageInfo> adapter = getRoute(code);
        if (adapter == null) return false;

        adapter.provide(damagee, data);
        return true;
    }



}
