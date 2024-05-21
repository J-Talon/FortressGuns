package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

public class RailGun extends HeavyArtillery {

    public RailGun(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }
}
