package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;

import me.camm.productions.fortressguns.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.core.Vector3f;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/*
Class that models a heavy machine gun which players can shoot and operate
 * @author CAMM
 *
 */
public class HeavyMachineGun extends RapidFire {

    protected static final double HEALTH, RANGE;
    protected static final ItemStack BARREL_ITEM, MUZZLE_ITEM, SEAT_ITEM;
    protected static final Vector3f rightArm, leftArm, body, rightLeg, leftLeg;

    static {
        HEALTH = 20;
        RANGE = 150;
        BARREL_ITEM = ArtilleryMaterial.STANDARD_BODY.asItem();
        MUZZLE_ITEM = ArtilleryMaterial.SMALL_BARREL.asItem();
        SEAT_ITEM = ArtilleryMaterial.SEAT.asItem();


        rightArm = new Vector3f(50,0,0);
                leftArm = new Vector3f(50,0,0);  body = new Vector3f(330,0,0);
                rightLeg = new Vector3f(267,0,0);  leftLeg = new Vector3f(267,0,0);

    }

    public HeavyMachineGun(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        barrel = new ArtilleryPart[4];
        base = new ArtilleryPart[1][1];
    }

    public ArtilleryPart getRotatingSeat(){
        return rotatingSeat;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return loadingInventory.getInventory();
    }

    @Override
    protected void spawnParts(){


        ArtilleryPart support;

        pivot = StandHelper.getCore(loc, BARREL_ITEM,aim,world, this);
        support = StandHelper.spawnVisiblePart(loc.clone().subtract(0,0.5,0),null,aim, world,this);
        support.setArms(true);
        support.setPose(rightArm, leftArm, body, rightLeg, leftLeg);

        base[0][0] = support;
        Location rotatingSeat = support.getLocation(world);

        double rotSeatZ = -Math.cos(aim.getY());
        double rotSeatX = Math.sin(aim.getY());

        rotatingSeat.add(rotSeatX,0.5,rotSeatZ);
        this.rotatingSeat = StandHelper.spawnPart(rotatingSeat, SEAT_ITEM,new EulerAngle(0,aim.getX(), 0),world,this);
        this.triggerHandle = StandHelper.spawnTrigger(rotatingSeat.clone().add(0,1,0),world, this);


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

        initLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);
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


        Location rotatingSeat = base[0][0].getLocation(world);

        double rotSeatZ = -Math.cos(horAngle);
        double rotSeatX = Math.sin(horAngle);

        rotatingSeat.add(rotSeatX,0,rotSeatZ);
        this.rotatingSeat.teleport(rotatingSeat.getX(),rotatingSeat.getY(), rotatingSeat.getZ());
        this.rotatingSeat.setRotation(0,(float)horAngle);
        this.triggerHandle.teleport(rotatingSeat.add(0,1,0));


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



    public void fireConversion(){
        int iterations = 0;
        long delayTicks = 2;
        final int shots = 3;

        if (!canFire())
            return;

        canFire = false;
        while (iterations < shots) {

            iterations ++;
            final int reference = iterations;
            new BukkitRunnable() {
                public void run() {
                    fireOperate();

                    if (reference == shots)
                        canFire = true;

                    cancel();


                }
            }.runTaskLater(plugin, iterations * delayTicks);
        }
    }


    private void fireOperate() {

        class PredicateEqual implements Predicate<Entity> {

            private final Player operator;
            private final Artillery artillery;
            public PredicateEqual(Player operator, Artillery artillery){
                this.operator = operator;
                this.artillery = artillery;
            }

            @Override
            public boolean test(Entity e) {
                net.minecraft.world.entity.Entity nms = ((CraftEntity)e).getHandle();
                if (nms instanceof ArtilleryPart) {
                    ArtilleryPart part = ((ArtilleryPart)nms);

                    return !part.getBody().equals(artillery);
                }

                return !e.equals(operator);
            }
        }

        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();

        Player operator = null;
        for (Entity e: passengers) {
            if (e instanceof Player)
            {
                operator = (Player) e;
                break;
            }
        }

        if (operator == null)
            return;


        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);


        createFlash(muzzle);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,1f,2f);

        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        projectileVelocity.setX(x);
        projectileVelocity.setY(y);
        projectileVelocity.setZ(z);
        projectileVelocity.normalize();

        Vector direction = projectileVelocity.clone();
        Vector origin = muzzle.toVector();

        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();


        //Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter
        RayTraceResult result = world.rayTrace(muzzle,projectileVelocity,getRange(), FluidCollisionMode.ALWAYS,true, 0.1,new PredicateEqual(operator,this));




        Location position = null;
        if (result == null) {
            canFire = true;
        }
        else {

            Block hitBlock = result.getHitBlock();
            Entity hit = result.getHitEntity();
            position = result.getHitPosition().toLocation(world);




            if (hitBlock != null && !hitBlock.getType().isAir()) {
                world.spawnParticle(Particle.BLOCK_CRACK, position, 10, 0, 0, 0, hitBlock.getBlockData());
                float hardness = hitBlock.getType().getHardness();

                if (hardness < Material.DIRT.getHardness() && hardness >= 0)
                    hitBlock.breakNaturally();

            }


            if (hit != null) {
                net.minecraft.world.entity.Entity nms = ((CraftEntity) hit).getHandle();
                nms.damageEntity(GunSource.gunShot(nmsOperator), 9);

                if (nms instanceof EntityLiving)
                    nms.W = 0;

            }
        }

        final Location hitPosCopy = position;

        new BukkitRunnable() {

            final double distance = hitPosCopy != null ? hitPosCopy.distance(muzzle) : getRange();
            double travelled = 0;

            public void run() {
                canFire = true;
                Location pivLoc = pivot.getLocation(world);
                Item item = world.dropItem(pivLoc,CASING);
                Vector vel = origin.clone().normalize();
                double x = vel.getX();
                double z = vel.getZ();
                vel.setX(z);
                vel.setZ(x);

                item.setVelocity(vel);


                do {
                    travelled += 1;
                    origin.add(direction);
                    world.spawnParticle(Particle.SMOKE_NORMAL,origin.getX(), origin.getY(), origin.getZ(),1,0,0,0,0);
                }
                while (travelled < distance);
            }
        }.runTaskLater(FortressGuns.getInstance(),1);



    }

    @Override
    public synchronized void fire(@Nullable Player shooter) {
      fireConversion();
    }



    @Override
    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        parts.add(pivot);
        parts.add(triggerHandle);
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


        return canFire;
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
