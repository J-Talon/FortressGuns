package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

public class CRAM extends RapidFire {


    public CRAM(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }

    @Override
    public void fire(@Nullable Player shooter) {

    }

    @Override
    public ArtilleryType getType() {
        return null;
    }

    @Override
    public boolean canFire() {
        return false;
    }

    @Override
    public double getMaxHealth() {
        return 0;
    }


}
