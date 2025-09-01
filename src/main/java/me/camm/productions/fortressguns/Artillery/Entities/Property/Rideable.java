package me.camm.productions.fortressguns.Artillery.Entities.Property;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.entity.Entity;

import java.util.List;

public interface Rideable {

    ComponentAS getSeat();

    void positionSeat();

    boolean hasRider();

    void rideTick(EntityHuman human);

    void setHasRider(boolean hasRider);

    void onDismount();

    void onMount();

    default void updateOnInteraction() {

    }

}
