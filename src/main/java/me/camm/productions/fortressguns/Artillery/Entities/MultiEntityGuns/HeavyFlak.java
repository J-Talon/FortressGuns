package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FlakArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeavyFlak extends FlakArtillery {


    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    //length of body before starting the barrel
    private static final int BODY_LENGTH = 3;



    static {

        HEALTH = 70;

        FIRE_COOLDOWN = 3000;

    }

    public HeavyFlak(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader,aim);
        barrel = new ArtilleryPart[10];
        base = new ArtilleryPart[4][4];
    }

    @Override
    public double nextHorizontalAngle(double currentAngle, double targetAngle) {

        currentAngle = Math.toDegrees(currentAngle);
        targetAngle = Math.toDegrees(targetAngle);

        if (Math.abs(currentAngle - targetAngle) <= 1)
            return Math.toRadians(targetAngle);

        //converting the current angle from -180 -> 180 format to 0->360 format
        if (currentAngle < 0)
            currentAngle += 360;

        if (targetAngle < 0)
            targetAngle += 360;

        double diffAngle = ((targetAngle - currentAngle + 540) % 360) - 180;
        int dir;

        if (diffAngle > 0)
            dir =  1;
        else if (diffAngle < 0)
            dir = -1;
        else
            dir = 0;

        double offset = Math.min(3,Math.abs(diffAngle));

        dir *= offset;
        currentAngle += dir;

        return Math.toRadians(currentAngle);

    }

    public synchronized void pivot(double vertAngle, double horAngle) //v = h.xRot  h = h.gHeadRot
    {
        if (dead)
            return;

        if (isInvalid()) {
            remove(false, true);
            dead = true;
            return;
        }


        //0.017 = 1 degree in rad form
        vertAngle = Math.abs(vertAngle - aim.getX()) <= 0.034? vertAngle : nextVerticalAngle(aim.getX(), vertAngle);

        //don't add PI to give an extra 180 * to the rotation (see Construct.getASFace(EntityHuman) )
        //since  -horizontalDistance*Math.sin(horAngle); already takes care of it.
        horAngle = Math.abs(horAngle - aim.getY()) <= 0.051 ? horAngle : nextHorizontalAngle(aim.getY(), horAngle);

        positionSeat(rotatingSeat,this);



        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot
            if (stand.isSmall())
                totalDistance = (largeBlockDist *0.75 + 0.5* smallBlockDist) + (slot * smallBlockDist);
            else
                totalDistance = (slot+1)* largeBlockDist;


            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);


            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);
            //the - is to account for the 180* between players and armorstands




            aim = new EulerAngle(vertAngle,horAngle,0);
            //setting the rotation of all of the barrel armorstands.
            stand.setRotation(aim);
            pivot.setRotation(aim);


            Location centre = pivot.getLocation(world).clone();

            //teleporting the armorstands to be in line with the pivot
            if (stand.isSmall()) {
                Location teleport = centre.add(x, height + 0.75, z);
                stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
            }
            else {
                Location teleport = centre.clone().add(x, height, z);
                stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
            }
        }
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
                    autoAim();
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
    protected void spawnParts() {

        pivot = StandHelper.getCore(loc, BODY,aim,world,this);
        pivot.setLocation(loc.getX(),loc.getY(),loc.getZ());
        rotatingSeat = StandHelper.spawnPart(getSeatSpawnLocation(this),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        //for the barrel
        for (int slot=0;slot< barrel.length;slot++)
        {
            boolean small = false;
            double totalDistance;

            if (slot>=BODY_LENGTH) {
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

      //  pivot(0,0);

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
    public ArtilleryType getType() {
        return ArtilleryType.FLAK_HEAVY;
    }

    @Override
    public double getMaxHealth() {
        return HEALTH;
    }


    /*

  Locations: the locations which an entity has been tracked for
  period: the period between each tracking time, in seconds
   */


    public boolean aimMoving(Location[] locations, int period){

        double threshold = 5;
        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);
            if (target == null || target.isRemoved() || !target.isAlive()) {
                target = null;
                return false;
            }

        Location targetLocation = new Location(world, target.locX(), target.locY(), target.locZ());

            Vector vAverage = new Vector(0,0,0);

            if (locations.length <= 1)
                return false;

           for (int slot = 0;slot< locations.length-1;slot++) {
               Location current = locations[slot];
               Location next = locations[slot+1];

               next.clone().subtract(current);
               vAverage.add(next.toVector());

           }

           vAverage.multiply(1/period);

           if (vAverage.clone().subtract(target.getBukkitEntity().getVelocity()).lengthSquared() > threshold*threshold) {
               autoAim();
               return true;
           }


          double distToTarget = muzzle.distance(targetLocation);

           if (distToTarget == 0) {
               autoAim();
               return true;
           }

        //time in seconds
           double timeToTarget = (vectorPower * 20)/distToTarget;

          Vector targVelocity = target.getBukkitEntity().getVelocity();
          targVelocity.clone().multiply(timeToTarget);

          Location predicted = targVelocity.toLocation(world).add(targetLocation);
          double deltaX = predicted.getX() - muzzle.getX();
          double deltaZ = predicted.getZ() - muzzle.getZ();
          double deltaY = predicted.getY() - muzzle.getY();


          double horAngle = deltaZ == 0 ? 0 : Math.atan(deltaX/deltaZ);
          double horDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
          double vertAngle = horDistance == 0 ? 90: Math.atan(deltaY/horDistance);


          horAngle = Math.toRadians(horAngle);
          vertAngle = Math.toRadians(vertAngle);

          pivot(vertAngle, horAngle);

            return true;
    }
}
