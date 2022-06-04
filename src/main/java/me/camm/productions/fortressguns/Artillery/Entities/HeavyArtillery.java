package me.camm.productions.fortressguns.Artillery.Entities;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HeavyArtillery extends FieldArtillery
{

    private static final double POWER;
    private static final int TIME;
    private static final double RECOVER_RATE;
    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    protected static ItemStack BODY = new ItemStack(Material.GREEN_TERRACOTTA);
    protected static ItemStack BASE_CLOSE = new ItemStack(Material.COAL_BLOCK);
    protected static ItemStack BASE_FAR = new ItemStack(Material.STONE_BRICK_SLAB);
    protected static ItemStack BARREL_MAT = new ItemStack(Material.DISPENSER);

    static {
        POWER = 6;
        TIME = 1;
        RECOVER_RATE = 0.05;
        HEALTH = 80;
        FIRE_COOLDOWN = 3000;
    }


    public HeavyArtillery(Location loc, World world, ChunkLoader loader) {
        super(loc, world,loader);
        barrel = new ArtilleryPart[8];
        base = new ArtilleryPart[4][3];
    }


    @Override
    public void fire(double power, int recoilTime, double barrelRecoverRate) {
      super.fire(power, recoilTime, barrelRecoverRate);
    }

    @Override
    public synchronized boolean canFire(){
        return canFire && System.currentTimeMillis() - lastFireTime >= FIRE_COOLDOWN;
    }

    @Override
    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        for (ArtilleryPart[] segment: base)
            parts.addAll(Arrays.asList(segment));
        parts.add(pivot);
        return parts;

    }


    @Override
    public void fire() {
      this.fire(POWER, TIME, RECOVER_RATE);
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
    public void spawn(){

        super.spawn();
        pivot = StandHelper.getCore(loc, BODY,aim,world,this);
        pivot.setLocation(loc.getX(),loc.getY(),loc.getZ());


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
                stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), BARREL_MAT, aim, world, this);
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
                    part = StandHelper.spawnPart(spawn, BASE_FAR, aim, world, this);
                else if (bar!=base.length-1)
                    part = StandHelper.spawnPart(spawn, BASE_CLOSE,aim,world, this);
                else
                    part = StandHelper.spawnPart(spawn, BODY,aim,world, this);

                length ++;
                standRow[slot] = part;
            }
            bar ++;
             rads += 2 * Math.PI / 4;
        }
        if (health <= 0)
            setHealth(HEALTH);

        initLoadedChunks();


     //   pivot(0,0);

    }
}
