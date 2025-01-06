package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.ExplosiveHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShell;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import javax.annotation.Nullable;
import java.util.*;

public abstract class FieldArtillery extends ArtilleryRideable
{

    private final double DIST_X = 0, DIST_Y = 0;

    protected static ItemStack BODY = ArtilleryMaterial.STANDARD_BODY.asItem();
    protected static ItemStack WHEEL = ArtilleryMaterial.WHEEL.asItem();
    protected static ItemStack SUPPORT = ArtilleryMaterial.BASE_SUPPORT.asItem();
    protected static ItemStack BARREL = ArtilleryMaterial.BARREL.asItem();
    protected static ItemStack SEAT = ArtilleryMaterial.SEAT.asItem();



    public FieldArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }


    @Override
    protected void initInventories() {
        interactionInv = new InventoryGroup.StandardGroup(this);

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

        setSmallDistance(0);

        canFire = false;

        final List<Player> vibrateFor = getVibratedPlayers();
        Artillery source = this;

        new BukkitRunnable()
        {
            boolean shot = false;
            int ticks = 0;

            @Override
            public void run() {

                if (!shot) {
                    shot = true;
                    EntityPlayer shooterNMS = shooter == null ? null : ((CraftPlayer)shooter).getHandle();
                    HeavyShell shell = (HeavyShell)createProjectile(((CraftWorld)world).getHandle(),muzzle.getX(), muzzle.getY(), muzzle.getZ(), shooterNMS, source);

                    if (shell == null) {
                        cancel();
                        return;
                    }

                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);

                    setAmmo(Math.max(0, getAmmo() - 1));
                }



                //pivot is already called if there is a rider so don't call pivot twice
                if (!hasRider()) {
                    pivot(aim.getX(), aim.getY());
                }

                vibrateAnimation(vibrateFor,ticks, 1);

                if (smallBlockDist < SMALL_BLOCK_LENGTH) {
                    ticks ++;
                    //sin(PIx + (Pi/2)) * max(-0.1x + 1, 0)

                    //basically makes the barrel recover faster as it progresses, starting out slow
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
                    setSmallDistance(SMALL_BLOCK_LENGTH);
                    canFire = true;
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(), 4, recoilTime);

    }


    @Override
    public void positionSeat() {
        if (rotatingSeat != null) {
            //Math.PI * 1.5 --> 90* angle. Technically it's 270 degrees though
            posSeatAbsoluteHorizon(rotatingSeat,DIST_X, DIST_Y, getVibrationOffsetY(),Math.PI*1.5);
        }
    }

    @Override
    public int getMaxAmmo() {
        return 1;
    }

    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.EXPLOSIVE_HEAVY == item || AmmoItem.STANDARD_HEAVY == item;
    }

    @Override
    public Component getSeat() {
        return rotatingSeat;
    }

    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
        lengthChanged = true;
    }

    protected synchronized void setSmallDistance(double dist) {
        this.smallBlockDist = dist;
        lengthChanged = true;
    }

    protected synchronized void incrementLargeDistance(double increment) {
        this.largeBlockDist += increment;
        lengthChanged = true;
    }

    protected synchronized void setLargeDistance(double dist) {
        this.largeBlockDist = dist;
        lengthChanged = true;
    }


    @Override
    protected boolean spawnParts()
    {

        pivot = StandHelper.createCore(loc, BODY, aim, world, this);

        //pivot.setRotation(aim);
        rotatingSeat = StandHelper.createInvisiblePart(getSeatLocation(DIST_X, DIST_Y,Math.PI*1.5),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        if (pivot == null || rotatingSeat == null) {
            return false;
        }

        if (!spawnTurretParts() || !spawnBaseParts()) {
            return false;
        }

        //for the base of the artillery
        calculateLoadedChunks();
        if (health <= 0)
            setHealth(getMaxHealth());


        return true;

    }



    /*
    bar is basically the dist from the center to the edge of the cannon, basically controlling whether
    a possible "overwrite" is possible of the legs

    rads is the angle to increment after every "drawing" of the legs


     */
    protected boolean spawnBaseWithDegrees(int bar, double rads, double defaultRadValue, double defaultRadInc, boolean useDefault) {
        for (ArtilleryPart[] standRow: base) {
            double[] position = getBasePositions(rads);  //get the x, z values for the base

            int length = 0;

            for (int slot=0;slot<standRow.length;slot++) {
                Location loc = pivot.getLocation(this.world).clone().
                        add((position[0] * LARGE_BLOCK_LENGTH + length*position[0]),
                              -0.75,    //-0.75 makes stuff look nice (from testing)
                                (LARGE_BLOCK_LENGTH * position[1] + length*position[1]));
                /*
                I know, there's z fighting for the closest blocks cause of the distance
                the alternative is making it hollow in the middle, so pick your poison

                 */


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


            //totalDistance = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);
            /*
            total distance = the number of large blocks + the number of small blocks + correction factor
            The correction factor 0.5 is because of the awkward positioning from the transition from the large to small blocks

            we could use correctionFactor == small block length but if we use 0.1 more than it we get a barrel that's
            slightly more visible.

            totalDist = (large block length * smallThresh) + ((slot - smallThresh) * small block length) + 0.45
                              ^                                   ^                                         ^
                              the amount of large blocks         current small blocks                 correction

            l = large block length
            t = smallthresh
            x = slot
            s = small block length

            totalDist = lt + ((x - t) * s) + 0.45
                      = lt + (xs - ts) + 0.45
                      = lt + xs - ts + 0.45

                      = 0.6t + xs - 0.4t + 0.45
                      = 0.2t + xs + 0.45
                      = 0.2 + x + 0.45
                      = 0.65 + x


             The old implementation for the artillery was:
            totalDistance = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);
                          = 0.75l + 0.5s + xs
                          = 0.75(0.6) + 0.5(0.4) + xs
                          = 0.2 + 0.45 + xs
                          = 0.65 + xs
                          = 0.65 + x

            */

            if (slot >= smallThresh) {
                totalDistance = (LARGE_BLOCK_LENGTH * smallThresh) + ((slot - smallThresh) * SMALL_BLOCK_LENGTH) + 0.45;
                small = true;
            }
            else
                totalDistance = (slot + 1) * LARGE_BLOCK_LENGTH;

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();


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
