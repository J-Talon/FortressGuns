package me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

public interface SideSeated {

    default void positionSeat(ArtilleryPart part, Artillery artillery) {
    positionSeat(part, artillery, 0,0);
    }

    default void positionSeat(ArtilleryPart part, Artillery artillery, double xOffset, double yOffset) {
        EulerAngle aim = artillery.getAim();
        Location next = getSeatSpawnLocation(artillery, xOffset, yOffset);
        EulerAngle seatFacing = new EulerAngle(0,aim.getY(),0);
        part.setRotation(seatFacing);
        part.teleport(next);
    }



    default Location getSeatSpawnLocation(Artillery artillery){
        return getSeatSpawnLocation(artillery, 0,0);
    }

    default Location getSeatSpawnLocation(Artillery artillery, double xOffset, double yOffset) {
        double seatAngle = Math.PI*1.5 + artillery.getAim().getY(); //get 90* offset
        double seatDistance = artillery.getBaseLength()*0.25;  //0.25 is for distance

        Location center = artillery.getPivot().getLocation(artillery.getWorld());

        double seatZ = seatDistance*Math.cos(seatAngle);
        double seatX = -seatDistance*Math.sin(seatAngle);

        seatZ = seatZ + (seatZ * xOffset);
        seatX = seatX + (seatX * xOffset);

        return center.clone().add(seatX, yOffset, seatZ);
    }


}
