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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LightArtillery extends FieldArtillery
{

    private static final int SMALL_THRESH = 2;

    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    static {
        HEALTH = 40;
        FIRE_COOLDOWN = 1000;
    }


    public LightArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader,aim);
        barrel = new ArtilleryPart[5];
        base = new ArtilleryPart[3][3];
    }

    public synchronized boolean canFire(){
        return canFire && System.currentTimeMillis()-lastFireTime >= FIRE_COOLDOWN;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FIELD_LIGHT;
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }

    @Override
    protected void spawnBaseParts() {
        double rads = -Math.PI/3;
        int bar = 0;
        double radsIfTrue = Math.PI;
        double radsIfFalse = 2 * Math.PI / 3;

        super.spawnBaseWithDegrees(bar, rads, radsIfTrue, radsIfFalse, true);
    }


    @Override
    protected void spawnParts()
    {

        pivot = StandHelper.getCore(loc, BODY, aim, world, this);
        //pivot.setRotation(aim);
        rotatingSeat = StandHelper.spawnPart(getSeatSpawnLocation(this),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        super.spawnTurretParts();
        spawnBaseParts();

        //for the base of the artillery
        calculateLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);


    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return loadingInventory.getInventory();
    }

    @Override
    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        for (ArtilleryPart[] segment: base)
            parts.addAll(Arrays.asList(segment));
        parts.add(pivot);
        parts.add(rotatingSeat);
        return parts;

    }

    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }
}
