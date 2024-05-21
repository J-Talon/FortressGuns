package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
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
    protected boolean spawnParts(){


        pivot = StandHelper.createCore(loc, BODY,aim,world,this);
        if (pivot == null)
            return false;

        pivot.setLocation(loc.getX(),loc.getY(),loc.getZ());
        rotatingSeat = StandHelper.createInvisiblePart(getSeatSpawnLocation(this),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        if (rotatingSeat == null)
            return false;

        if (!super.spawnTurretParts() || !this.spawnBaseParts())
            return false;

        calculateLoadedChunks();

        if (health <= 0)
            setHealth(HEALTH);

        return true;
    }




    @Override
    protected boolean spawnBaseParts() {
        double rads = 0;
        int bar = 0;
        return super.spawnBaseWithDegrees(bar, rads, -1, 2 * Math.PI / 4, false);
    }


    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }
}
