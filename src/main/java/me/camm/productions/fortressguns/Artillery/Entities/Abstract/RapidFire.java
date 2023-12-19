package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.BulkLoaded;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.BackSeated;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.core.Vector3f;
import org.bukkit.*;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.util.EulerAngle;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * @author CAMM
 */
public abstract class RapidFire extends Artillery implements BulkLoaded, BackSeated {


    protected static final ItemStack BARREL_ITEM, MUZZLE_ITEM, SEAT_ITEM;
    protected static final Vector3f rightArm, leftArm, body, rightLeg, leftLeg;

    protected static final double HEALTH;

    static {
        HEALTH = 20;

        BARREL_ITEM = ArtilleryMaterial.STANDARD_BODY.asItem();
        MUZZLE_ITEM = ArtilleryMaterial.SMALL_BARREL.asItem();
        SEAT_ITEM = ArtilleryMaterial.SEAT.asItem();


        rightArm = new Vector3f(50,0,0);
        leftArm = new Vector3f(50,0,0);  body = new Vector3f(330,0,0);
        rightLeg = new Vector3f(267,0,0);  leftLeg = new Vector3f(267,0,0);

    }

    protected Vector projectileVelocity;
    protected static ItemStack CASING;


    protected FireTrigger triggerHandle;

    static {
        CASING = new ItemStack(Material.IRON_NUGGET);
        if (CASING.getItemMeta() != null) {
            ItemMeta meta = CASING.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Bullet Casing");
            CASING.setItemMeta(meta);

        }
    }




    public RapidFire(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        barrel = new ArtilleryPart[4];
        base = new ArtilleryPart[1][1];
        projectileVelocity = new Vector(0,0,0);
    }

    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        parts.add(pivot);
        parts.add(triggerHandle);
        parts.addAll(Arrays.asList(base[0]));
        parts.add(rotatingSeat);

        return parts;
    }


    @Override
    protected void spawnTurretParts() {

        for (int slot = 0;slot < barrel.length;slot++) {
            double totalDistance = (0.5 * LARGE_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), MUZZLE_ITEM, aim, world, this);
            stand.setSmall(true);

            barrel[slot] = stand;
        }
    }

    @Override
    protected void positionSeat() {

        // aim = new EulerAngle(vertAngle,horAngle,0);
        Location rotatingSeat = base[0][0].getLocation(world);
        EulerAngle aim = this.getAim();
        double horAngle = aim.getY();

        double rotSeatZ = -Math.cos(horAngle);
        double rotSeatX = Math.sin(horAngle);

        rotatingSeat.add(rotSeatX,0,rotSeatZ);
        this.rotatingSeat.teleport(rotatingSeat.getX(),rotatingSeat.getY(), rotatingSeat.getZ());
        this.rotatingSeat.setRotation(0,(float)horAngle);
        this.triggerHandle.teleport(rotatingSeat.add(0,1,0));
    }


    @Override
    protected void spawnParts(){

        pivot = StandHelper.getCore(loc, BARREL_ITEM,aim,world, this);
        spawnBaseParts();

        ArtilleryPart support = base[0][0];

        Location seatPos = getSeatLocation(this, support);
        seatPos.add(0,0.5,0);

        this.rotatingSeat = StandHelper.spawnPart(seatPos, SEAT_ITEM,new EulerAngle(0,aim.getX(), 0),world,this);
        this.triggerHandle = StandHelper.spawnTrigger(seatPos.clone().add(0,1,0),world, this);


        spawnTurretParts();

        calculateLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);
    }



    @Override
    protected void spawnBaseParts() {
        ArtilleryPart support;
        support = StandHelper.spawnVisiblePart(loc.clone().subtract(0,0.5,0),null,aim, world,this);
        support.setArms(true);
        support.setPose(rightArm, leftArm, body, rightLeg, leftLeg);
        base[0][0] = support;
    }





    @Override
    public synchronized void pivot(double vertAngle, double horAngle)
    {
        if (dead)
            return;

        if (isInvalid()) {
            remove(false, true);
            dead = true;
            return;
        }

        positionSeat();


        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot

            totalDistance = (0.5*LARGE_BLOCK_LENGTH) + (slot * smallBlockDist);

            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);


            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);




            aim = new EulerAngle(vertAngle,horAngle,0);
            //setting the rotation of all of the barrel armorstands.

            //rotating the face of the armorstand by 90 degrees.
            //   if (stand.isFacesDown())
            //        stand.setHeadPose(new Vector3f((float)aim.getX(),(float)(aim.getY()+PI/2),(float)aim.getZ()));

            stand.setRotation(aim);
            pivot.setRotation(aim);

            Location centre = pivot.getLocation(world).clone();

            //teleporting the armorstands to be in line with the pivot
            Location teleport = centre.add(x, height + 0.75, z);
            stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());

        }
    }





}
