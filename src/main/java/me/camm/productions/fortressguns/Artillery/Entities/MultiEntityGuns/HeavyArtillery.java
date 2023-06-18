package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.ArtilleryInventory;
import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import java.util.List;


public class HeavyArtillery extends FieldArtillery
{


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

        //for the barrel
        for (int slot=0;slot< barrel.length;slot++)
        {
            boolean small = false;
            double totalDistance;

            if (slot>=1) {
                totalDistance = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);
                small = true;
            }
            else
                totalDistance = (slot+1)* LARGE_BLOCK_LENGTH;

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();

            //if it is small, add 0.75 so that it is high enough
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            if (small) {
                stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), BARREL, aim, world, this);
                stand.setSmall(true);
            }
            else
                stand = StandHelper.spawnPart(centre.add(x, height, z), BODY,aim,world,this);

            barrel[slot] = stand;
        }

        double rads = 0;
        int bar = 0;

        //for the base of the artillery
        for (ArtilleryPart[] standRow: base) {
            double[] position = getBasePositions(rads);  //get the x, z values for the base

            int length = 0;

            for (int slot=0;slot<standRow.length;slot++) {
                Location spawn = pivot.getLocation(world).clone().add(position[0] + (length*position[0]), -0.75, position[1]+(length*position[1]));

                ArtilleryPart part;

                //if the length is close to base, then give it wheels, else give it supports
                if (length >=1)
                    part = StandHelper.spawnPart(spawn, SUPPORT, null, world, this);
                else if (bar!=base.length-1)
                    part = StandHelper.spawnPart(spawn, WHEEL,null,world, this);
                else
                    part = StandHelper.spawnPart(spawn, BODY,null,world, this);

                length ++;
                standRow[slot] = part;
            }
            bar ++;
             rads += 2 * Math.PI / 4;
        }
        if (health <= 0)
            setHealth(HEALTH);

        initLoadedChunks();


    }
}
