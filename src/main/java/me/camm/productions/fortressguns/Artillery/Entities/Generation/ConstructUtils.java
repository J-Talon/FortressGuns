package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ConstructUtils {


    //in the future, if we plan to make this version compatible with multiple versions,
    //this will help


    public static @Nullable Component getComponentRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (nms instanceof Component) return (Component)nms;
        return null;
    }



    public static @Nullable Rideable getRideableRef(Entity entity) {
        if (entity == null) return null;
        net.minecraft.world.entity.Entity nms = ((CraftEntity)entity).getHandle();
        if (!(nms instanceof Component comp)) return null;

        Construct body = comp.getBody();
        if (body instanceof Rideable ride) return ride;
        return null;
    }

    //returns whether the entity is the seat of the rideable
    public static boolean isSeatOfConstruct(Rideable rideable, Entity ref) {
        Component comp = getComponentRef(ref);
        if (comp == null) return false;
        return rideable.getSeat().equals(comp);
    }

}
