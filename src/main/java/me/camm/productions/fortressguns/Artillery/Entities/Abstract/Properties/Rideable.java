package me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import net.minecraft.world.entity.player.EntityHuman;

public interface Rideable {

    abstract Component getSeat();
    abstract void positionSeat();

    abstract boolean hasRider();

    abstract void rideTick(EntityHuman human);

    abstract void setHasRider(boolean hasRider);

    default void updateOnInteraction() {

    }

}
