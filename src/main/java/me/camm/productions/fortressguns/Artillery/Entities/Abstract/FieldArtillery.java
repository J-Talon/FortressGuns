package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.SideSeated;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.ExplosiveHeavyShell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import javax.annotation.Nullable;
import java.util.*;

public abstract class FieldArtillery extends Artillery implements SideSeated
{


    protected static ItemStack BODY = ArtilleryMaterial.STANDARD_BODY.asItem();
    protected static ItemStack WHEEL = ArtilleryMaterial.WHEEL.asItem();
    protected static ItemStack SUPPORT = ArtilleryMaterial.BASE_SUPPORT.asItem();
    protected static ItemStack BARREL = ArtilleryMaterial.BARREL.asItem();
    protected static ItemStack SEAT = ArtilleryMaterial.SEAT.asItem();



    public FieldArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }



    public synchronized void fire(@Nullable Player shooter)
    {
        if (isInvalid()) {
            remove(false, true);
            return;
        }

        if (canFire()) {
            lastFireTime = System.currentTimeMillis();
        }
        else
            return;

        //getting the location of the last armorstand in the barrel array
        final Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);

        //make a flash
        createFlash(muzzle);
        createShotParticles(muzzle);
        vibrateParticles();

        Vector velocity = eulerToVec(aim).normalize().multiply(getVectorPower());
        final Vec3D vector = new Vec3D(velocity.getX(),velocity.getY(), velocity.getZ());

        smallBlockDist = 0;
        canFire = false;

        final List<Player> vibrateFor = getVibratedPlayers();

        new BukkitRunnable()
        {
            boolean shot = false;
            int ticks = 0;

            @Override
            public void run() {

                if (!shot) {
                    shot = true;
                    ExplosiveHeavyShell shell = new ExplosiveHeavyShell(EntityTypes.d,muzzle.getX(),muzzle.getY(),muzzle.getZ(),((CraftWorld)world).getHandle(), shooter);
                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }



                //pivot is already called if there is a rider so don't call pivot twice
                if (!getHasRider()) {
                    pivot(aim.getX(), aim.getY());
                }

                vibrateAnimation(vibrateFor,ticks, 1);

                if (smallBlockDist < SMALL_BLOCK_LENGTH) {
                    ticks ++;
                    //sin(PIx + (Pi/2)) * max(-0.1x + 1, 0)
                    incrementSmallDistance(barrelRecoverRate * (Math.min(1,0.000125 * ticks * ticks * ticks)));
                    Location barrelEnd = barrel[barrel.length-1].getEyeLocation();
                    int count = (int)(Math.ceil(10 - (smallBlockDist / SMALL_BLOCK_LENGTH) * 10));

                    for (int spawned = 0; spawned < count; spawned ++) {
                        world.spawnParticle(Particle.SMOKE_NORMAL, barrelEnd.clone().add(0, SMALL_BLOCK_LENGTH / 2, 0), 0, 0, 0.1, 0, 0.3);
                    }
                }
                else
                {
                    setVibrationOffsetY(0);
                    smallBlockDist = SMALL_BLOCK_LENGTH;
                    canFire = true;
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(), 4, recoilTime);

    }


    @Override
    protected void positionSeat() {
        if (rotatingSeat != null) {
            //implements SideSeated
            positionSeat(rotatingSeat,this, getVibrationOffsetY());
        }
    }

    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
    }


    protected boolean spawnBaseWithDegrees(int bar, double rads, double defaultRadValue, double defaultRadInc, boolean useDefault) {
        for (ArtilleryPart[] standRow: base) {
            double[] position = getBasePositions(rads);  //get the x, z values for the base

            int length = 0;

            for (int slot=0;slot<standRow.length;slot++) {
                Location loc = pivot.getLocation(this.world).clone().
                        add(
                                (position[0]*LARGE_BLOCK_LENGTH+length*position[0]),
                              -0.75,
                                (LARGE_BLOCK_LENGTH*position[1]+length*position[1]));


                //(World world, Artillery body, double d0, double d1, double d2)
                ArtilleryPart part;

                //if the length is close to base, then give it wheels, else give it
                //supports
                if (length >=1)
                    part = StandHelper.createInvisiblePart(loc, SUPPORT,null,world,this);
                else if (bar!=base.length-1)
                    part = StandHelper.createInvisiblePart(loc, WHEEL,null,world, this);
                else
                    part = StandHelper.createInvisiblePart(loc,BODY,null,world, this);

                if (part == null)
                    return false;

                length ++;
                standRow[slot] = part;
            }
            bar ++;

            if (useDefault && bar == base.length-1)
                rads = defaultRadValue;
            else
                rads += defaultRadInc;
        }
        return true;
    }



    @Override
    protected boolean spawnTurretParts() {

        int smallThresh = getSmallDistThreshold();

        //for the barrel
        for (int slot=0;slot< barrel.length;slot++)
        {
            boolean small = false;
            double totalDistance;

            if (slot>=smallThresh) {
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
                stand = StandHelper.createInvisiblePart(centre.add(x, height + 0.75, z), BARREL, aim, world, this);
                if (stand == null)
                    return false;

                stand.setSmall(true);
            }
            else {
                stand = StandHelper.createInvisiblePart(centre.add(x, height, z), BODY, aim, world, this);
                if (stand == null)
                    return false;
            }

            barrel[slot] = stand;
        }
        return true;
    }





    protected abstract int getSmallDistThreshold();
}
