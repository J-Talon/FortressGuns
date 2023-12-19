package me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

public interface BackSeated {

    default Location getSeatLocation(Artillery arty, ArtilleryPart referencePos) {

        Location reference = referencePos.getLocation(arty.getWorld());
        EulerAngle aim = arty.getAim();
        double rotSeatZ = -Math.cos(aim.getY());
        double rotSeatX = Math.sin(aim.getY());
        return reference.clone().add(rotSeatX, 0, rotSeatZ);

    }

}
