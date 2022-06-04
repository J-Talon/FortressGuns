package me.camm.productions.fortressguns.Artillery.Entities;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;

import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.core.Vector3f;

import org.bukkit.*;

import org.bukkit.inventory.ItemStack;

import org.bukkit.util.EulerAngle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static java.lang.Math.PI;

/**
 * @author CAMM
 */
public class HeavyMachineGun extends RapidFire {

    protected static final double HEALTH, RANGE;
    protected static final long FIRE_COOLDOWN;
    protected static final ItemStack BARREL, MUZZLE, SEAT;
    protected static final Vector3f rightArm, leftArm, body, rightLeg, leftLeg;

    static {
        HEALTH = 20;
        RANGE = 150;
        BARREL = new ItemStack(Material.GREEN_TERRACOTTA);
        MUZZLE = new ItemStack(Material.NETHER_BRICK_FENCE);
        SEAT = new ItemStack(Material.OAK_TRAPDOOR);


        rightArm = new Vector3f(50,0,0);
                leftArm = new Vector3f(50,0,0);  body = new Vector3f(330,0,0);
                rightLeg = new Vector3f(267,0,0);  leftLeg = new Vector3f(267,0,0);

        FIRE_COOLDOWN = 100;
    }

    public HeavyMachineGun(Location loc, World world, ChunkLoader loader) {
        super(loc, world, loader);
        barrel = new ArtilleryPart[4];
        base = new ArtilleryPart[1][1];
    }

    public ArtilleryPart getRotatingSeat(){
        return rotatingSeat;
    }



    @Override
    public void spawn(){
        super.spawn();

        ArtilleryPart support;
        pivot = StandHelper.getCore(loc,BARREL,aim,world, this);
        support = StandHelper.spawnPart(loc.clone().subtract(0,0.5,0),null,aim, world,this);
        support.setInvisible(false);
        support.setBasePlate(false);

        base[0][0] = support;

        Location rotatingSeat = support.getLocation(world);

        double rotSeatZ = -Math.cos(aim.getY());
        double rotSeatX = Math.sin(aim.getY());

        rotatingSeat.add(rotSeatX,0.5,rotSeatZ);
        this.rotatingSeat = StandHelper.spawnPart(rotatingSeat,SEAT,aim,world,this);

        support.setArms(true);
        support.setPose(rightArm, leftArm, body, rightLeg, leftLeg);






        boolean down = false;
        for (int slot = 0;slot < barrel.length;slot++) {

            if (slot >= 2)
                down = true;

        double totalDistance = (0.5 * LARGE_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), MUZZLE, aim, world, this);
            stand.setSmall(true);

            if (down)
                stand.setFacesDown(true);

            barrel[slot] = stand;
        }

        initLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);
       // pivot(0,0);
    }


    @Override
    public synchronized void pivot(double vertAngle, double horAngle)
    {
        if (dead)
            return;

        if (inValid()) {
            remove(false, true);
            dead = true;
            return;
        }


        Location rotatingSeat = base[0][0].getLocation(world);

        double rotSeatZ = -Math.cos(horAngle);
        double rotSeatX = Math.sin(horAngle);

        rotatingSeat.add(rotSeatX,0,rotSeatZ);
        this.rotatingSeat.teleport(rotatingSeat.getX(),rotatingSeat.getY(), rotatingSeat.getZ());
        this.rotatingSeat.setRotation(0,(float)horAngle);


        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot

            totalDistance = (0.5*LARGE_BLOCK_LENGTH) + (slot * currentSmallLength);

            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);


            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);




            aim = new EulerAngle(vertAngle,horAngle,0);
            //setting the rotation of all of the barrel armorstands.

            if (stand.isFacesDown())
                stand.setHeadPose(new Vector3f((float)aim.getX(),(float)(aim.getY()+PI/2),(float)aim.getZ()));

                stand.setRotation(aim);

            pivot.setRotation(aim);



            Location centre = pivot.getLocation(world).clone();

            //teleporting the armorstands to be in line with the pivot
            Location teleport = centre.add(x, height + 0.75, z);
            stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());

        }
    }

    @Override
    public void fire(double power, int recoilTime, double barrelRecoverRate) {
       super.fire();
    }

    @Override
    public synchronized void fire() {
        super.fire();
    }



    @Override
    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        parts.add(pivot);
        parts.addAll(Arrays.asList(base[0]));
        parts.add(rotatingSeat);

        return parts;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.HEAVY_MACHINE;
    }

    @Override
    public boolean canFire() {


        return canFire && System.currentTimeMillis() - lastFireTime >= FIRE_COOLDOWN;
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }

    @Override
    public double getRange() {
        return RANGE;
    }
}
