package me.camm.productions.fortressguns.Artillery.Entities.Property;

import org.bukkit.entity.Entity;

public interface IAimed {

    public Entity getTarget();

    public void setTarget(Entity target);

}
