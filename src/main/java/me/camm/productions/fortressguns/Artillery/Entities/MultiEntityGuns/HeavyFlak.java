package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FlakArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;


public class HeavyFlak extends FlakArtillery {

    private static final int SMALL_THRESH = 3;
    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    //length of body before starting the barrel
   // private static final int BODY_LENGTH = 3;

    static {
        HEALTH = 70;
        FIRE_COOLDOWN = 3000;
    }

    public HeavyFlak(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader,aim);
        barrel = new ArtilleryPart[12];
        base = new ArtilleryPart[4][4];

        this.vertRotSpeed = 3;
        this.horRotSpeed = 2;
    }


    @Override
    public boolean canFire(){
        return canFire && System.currentTimeMillis() - lastFireTime >= FIRE_COOLDOWN;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return loadingInventory.getInventory();
    }


    public void startAiming() {

        if (isAiming())
            return;

        setAiming(true);

            new BukkitRunnable() {
                public void run() {

                    if (!isAiming()) {
                        cancel();
                        return;
                    }
                    aimStatic();
                }
            }.runTaskTimer(FortressGuns.getInstance(), 0, 1);
    }

    public void setAiming(boolean aiming) {
        this.aiming = aiming;
    }

    public boolean isAiming() {
        return aiming;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FLAK_HEAVY;
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }

    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }
}
