package me.camm.productions.fortressguns.Core.Artillery.Entities.Property;

import org.bukkit.entity.Entity;

public interface ITracking {

    public Entity getTarget();

    public void setTarget(Entity target);

}
