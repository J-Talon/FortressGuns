package me.camm.productions.fortressguns.Artillery.Entities.Property;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

//we may need to make this multi-rider compatible in the future if we wanna add some cool stuff
// but for now, keep it at 1
public interface IRideable {

    Component getSeat();

    boolean positionSeat();

    void rideTick(Player human);
    //returns whether the seat cannot be interacted with independent of
    //whether there is an operator or not
    default boolean isSeatLocked() {return false;}

    //it's entirely possible someone uses commands to put a thing on a component
    //but we ignore that
    @Nullable Player getRider();

    void onDismount(Player entity);

    boolean onMount(Player entity);

    void kickOperator();
}
