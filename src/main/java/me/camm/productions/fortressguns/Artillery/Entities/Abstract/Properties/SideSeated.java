package me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

public interface SideSeated {

    default void positionSeat(ArtilleryPart part, Artillery artillery) {
        EulerAngle aim = artillery.getAim();
        Location next = getSeatSpawnLocation(artillery);
        EulerAngle seatFacing = new EulerAngle(0,aim.getY(),0);
        part.setRotation(seatFacing);
        part.teleport(next);
    }

    default Location getSeatSpawnLocation(Artillery artillery){
        ArtilleryPart[][] base = artillery.getBase();


        double seatAngle = Math.PI*1.5 + artillery.getAim().getY(); //get 90* offset
        double seatDistance = base[0].length*0.25;

        Location center = artillery.getPivot().getLocation(artillery.getWorld());

        double seatZ = seatDistance*Math.cos(seatAngle);
        double seatX = -seatDistance*Math.sin(seatAngle);

        return center.clone().add(seatX, 0, seatZ);



    }
}
