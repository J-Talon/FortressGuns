package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SurfaceToAir extends Artillery {


    public SurfaceToAir(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        throw new UnsupportedOperationException("unfinished");
    }

    @Override
    public List<ArtilleryPart> getParts() {
        return null;
    }

    @Override
    public void fire(double power, int recoilTime, double barrelRecoverRate, @Nullable Player player) {

    }

    @Override
    public void fire(@Nullable Player shooter) {

    }

    @Override
    protected void init() {

    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.SURFACE_TO_AIR;
    }

    @Override
    public boolean canFire() {
        return false;
    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
