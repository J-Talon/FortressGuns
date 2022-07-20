package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FlakArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HeavyFlak extends FlakArtillery {


    private static final double POWER;
    private static final int TIME;
    private static final double RECOVER_RATE;
    private static final double HEALTH;
    private static final long FIRE_COOLDOWN;

    //length of body before starting the barrel
    private static final int BODY_LENGTH = 3;





    protected static ItemStack BODY = new ItemStack(Material.RED_TERRACOTTA);
    protected static ItemStack BASE_CLOSE = new ItemStack(Material.COAL_BLOCK);
    protected static ItemStack BASE_FAR = new ItemStack(Material.STONE_BRICK_SLAB);
    protected static ItemStack BARREL_MAT = new ItemStack(Material.DISPENSER);


    static {
        POWER = 6;
        TIME = 1;
        RECOVER_RATE = 0.05;
        HEALTH = 70;

        FIRE_COOLDOWN = 3000;

    }

    public HeavyFlak(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader,aim);
        barrel = new ArtilleryPart[10];
        base = new ArtilleryPart[4][4];
    }

    @Override
    public boolean canFire(){
        return canFire && System.currentTimeMillis() - lastFireTime >= FIRE_COOLDOWN;
    }

    @Override
    public void fire(@Nullable Player shooter) {
        super.fire(POWER,TIME,RECOVER_RATE, shooter);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory.getInventory();
    }

    public void setTarget(Entity target){
        List<Entity> passengers = this.pivot.getPassengers();

        if (passengers.size()> 0) {
            this.target = null;

            for (Entity e: passengers) {
                if (e instanceof EntityPlayer) {
                    e.playSound(SoundEffects.mm,2,1);
                    e.sendMessage(new ChatMessage(ChatColor.GOLD + "Someone wants to have the artillery auto-aim," +
                            " but it cannot do so if you're riding it!"), UUID.randomUUID());
                }

            }
        }
        else {
            this.target = target;

        }
    }


    public void toggleTargetingSimple(boolean autoShoot) {

        aiming = !aiming;

        if (aiming) {
            new BukkitRunnable() {
                public void run() {

                    if (!aiming) {
                        cancel();
                        return;
                    }


                    autoAim();


                    if (autoShoot && canFire()) {
                        fire(null);
                    }

                }
            }.runTaskTimer(FortressGuns.getInstance(), 0, 1);
        }

    }

    @Override
    protected void init() {

        pivot = StandHelper.getCore(loc, BODY,aim,world,this);
        pivot.setLocation(loc.getX(),loc.getY(),loc.getZ());

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
                    part = StandHelper.spawnPart(spawn, BASE_FAR, null, world, this);
                else if (bar!=base.length-1)
                    part = StandHelper.spawnPart(spawn, BASE_CLOSE,null,world, this);
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

    @Override
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
           double timeToTarget = (POWER * 20)/distToTarget;

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
