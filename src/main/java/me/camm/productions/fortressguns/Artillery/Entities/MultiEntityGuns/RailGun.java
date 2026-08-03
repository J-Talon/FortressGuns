package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Property.Tuneable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

public class RailGun extends HeavyArtillery implements Tuneable {
    public RailGun(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
    }
}
