package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class HeavyArtillery extends FieldArtillery
{

    private static final int SMALL_THRESH = 1;
    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    static {
        HEALTH = 80;
        FIRE_COOLDOWN = 3000;
    }

    public HeavyArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader, aim);
        barrel = new ArtilleryPart[8];
        base = new ArtilleryPart[4][3];
    }

    ///
    @Override
    public synchronized boolean canFire(){
        return canFire && System.currentTimeMillis() - lastFireTime >= FIRE_COOLDOWN;
    }

    @Override
    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = super.getParts();
        parts.add(pivot);
        parts.add(rotatingSeat);
        return parts;
    }

    public @NotNull Inventory getInventory(){
        return loadingInventory.getInventory();
    }


    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FIELD_HEAVY;
    }


    @Override
    protected synchronized void incrementSmallDistance(double increment) {
        super.incrementSmallDistance(increment);
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }

    @Override
    protected void spawnParts(){
        pivot = StandHelper.getCore(loc, BODY,aim,world,this);
        pivot.setLocation(loc.getX(),loc.getY(),loc.getZ());
        rotatingSeat = StandHelper.spawnPart(getSeatSpawnLocation(this),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        super.spawnTurretParts();
        this.spawnBaseParts();

        calculateLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);

    }




    @Override
    protected void spawnBaseParts() {
        double rads = 0;
        int bar = 0;
        super.spawnBaseWithDegrees(bar, rads, -1, 2 * Math.PI / 4, false);
    }


    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }
}
