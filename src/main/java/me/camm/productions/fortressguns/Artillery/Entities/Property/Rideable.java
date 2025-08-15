package me.camm.productions.fortressguns.Artillery.Entities.Property;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import net.minecraft.world.entity.player.EntityHuman;

public interface Rideable {

    Component getSeat();
    void positionSeat();

    boolean hasRider();

    void rideTick(EntityHuman human);

    void setHasRider(boolean hasRider);

    void onDismount();

    void onMount();

    default void updateOnInteraction() {

    }

}
