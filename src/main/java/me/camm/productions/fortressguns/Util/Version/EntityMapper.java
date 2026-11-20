package me.camm.productions.fortressguns.Util.Version;

import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityMapper {

    private static EntityMapper instance = null;
    private final Map<Version, VersionProvider<Object, Entity>> routes;


    private EntityMapper() {
        routes = new HashMap<>();
    }


    public static EntityMapper getInstance() {
        if (instance == null)
            instance = new EntityMapper();
        return instance;
    }



    public void addRoute(VersionProvider<Object, Entity> route) {
        Version version = route.getTarget();
        if (routes.containsKey(version))
            throw new IllegalStateException("Entity mapper already contains route for version "+version.getVersionCode());

        routes.put(version, route);
    }


    public @Nullable Object accept(Entity e) {
        Version version = FortressGuns.getVersionCode();
        VersionProvider<Object, Entity> provider = routes.getOrDefault(version, null);
        if (provider == null)
            throw new IllegalArgumentException("Version "+ version.getVersionCode() +" has no route!");

        return provider.provide(e);
    }




}
