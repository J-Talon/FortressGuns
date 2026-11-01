package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import me.camm.productions.fortressguns.Util.vfx.FG_VFX;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ArtilleryMaterial;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.StandHelper;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.camm.productions.fortressguns.Util.Math.MathFG.nextVerticalAngle;

public abstract class FieldArtillery extends ArtilleryRideable
{

    private final double DIST_X = 0, DIST_Y = 0;
    protected double smallBlockDist;//

    protected static ItemStack BODY = ArtilleryMaterial.STANDARD_BODY.asItem();
    protected static ItemStack WHEEL = ArtilleryMaterial.WHEEL.asItem();
    protected static ItemStack SUPPORT = ArtilleryMaterial.BASE_SUPPORT.asItem();
    protected static ItemStack BARREL = ArtilleryMaterial.BARREL.asItem();
    protected static ItemStack SEAT = ArtilleryMaterial.SEAT.asItem();


    protected final double BARREL_RECOVER_RATE = 0.03;
    protected final int RECOIL_TIME = 1;



    public FieldArtillery(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);

        smallBlockDist = SMALL_BLOCK_LENGTH;
    }


    @Override
    protected void initInventories() {
        interactionInv = new InventoryGroup.StandardGroup(this);
    }


    protected int getHeavyFireDelay() {
     return 4;
    }





    @Override
    protected @Nullable ProjectileFG createProjectile(World world, double x, double y, double z, Player shooter, Artillery source) {
        ProjectileFG shell = super.createProjectile(world, x, y, z, shooter, source);
        if (shell == null)
            return null;

        Vector vec = MathFG.eulerToVec(aim).normalize().multiply(getVectorPower());
        shell.setVelocity(vec.getX(), vec.getY(), vec.getZ());
        return shell;
    }


    protected List<Player> getShakenPlayers() {
        List<IntTuple2> chunks = getOccupiedChunks();
        final List<Player> vibrateFor = new LinkedList<>();
        for (IntTuple2 tup: chunks) {

            //it's fine cause at this point it should already be loaded
            Chunk c = world.getChunkAt(tup.getA(), tup.getB());
            org.bukkit.entity.Entity[] entities = c.getEntities();

            for (org.bukkit.entity.Entity e: entities) {
                if (e instanceof Player && e.getVehicle() == null)
                    vibrateFor.add((Player)e);
            }
        }
        return vibrateFor;
    }


    @Override
    public boolean pivot(double vertAngle, double horAngle)
    {

        if (isInvalid()) {
            destroy(false, true);
            dead = true;
            return false;
        }


        double currX,currY;
        currX = aim.getX();
        currY = aim.getY();


        //90 degrees in the up and down directions
        //in radians

        if (vertAngle <= getMaxVertAngle()) {
            vertAngle = Math.max(getMaxVertAngle(), vertAngle);
        }

        if (vertAngle >= getMinVertAngle()) {
            vertAngle = Math.min(vertAngle, getMinVertAngle());
        }

        final double TWO_PI = 2 * Math.PI;
        if (horAngle > TWO_PI || horAngle < -TWO_PI) {
            horAngle = horAngle % TWO_PI;
        }


        if (currX == vertAngle && currY == horAngle && !lengthChanged)
            return false;

        lengthChanged = false;

        vertAngle = nextVerticalAngle(currX, vertAngle, vertRotSpeed);


        //don't add PI to give an extra 180 * to the rotation (see StandHelper.getASFace(EntityHuman) )
        //since  -horizontalDistance*Math.sin(horAngle); already takes care of it.
        horAngle = MathFG.nextHorizontalAngle(currY, horAngle, horRotSpeed);

        Vector[] locations = new Vector[barrel.length];

        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart barrelComponent = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot
            if (barrelComponent.isSmall())
                totalDistance = (MathFG.LARGE_BLOCK_LENGTH *0.75 + 0.5* smallBlockDist) + (slot * smallBlockDist);
            else
                totalDistance = (slot+1)* MathFG.LARGE_BLOCK_LENGTH;


            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);



            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);
            //the - is to account for the 180* between players and armorstands

            //setting the rotation of all of the barrel armorstands.
            barrelComponent.setRotation(aim);

            Location centre = pivot.getLocation();

            Vector teleport;
            if (barrelComponent.isSmall())
                teleport = centre.add(x, height + 0.75, z).toVector();
            else
                teleport = centre.add(x, height, z).toVector();


            //is at resting state
            if (!(smallBlockDist < SMALL_BLOCK_LENGTH )) {
                if (ConstructUtils.isInBlock(barrelComponent, teleport)) {
                    return false;
                }
            }
            locations[slot] = teleport;
        }

        positionSeat();

        aim = new EulerAngle(vertAngle,horAngle,0);
        pivot.setRotation(aim);

        for (int slot=0;slot< barrel.length;slot++) {
            ArtilleryPart barrelComponent = barrel[slot];
            Vector teleport = locations[slot];
            barrelComponent.teleport(teleport.getX(), teleport.getY(), teleport.getZ());
        }
        return true;
    }




    public synchronized void fire(@Nullable Player shooter)
    {
        if (isInvalid()) {
            destroy(false, true);
            return;
        }

        if (canFire()) {
            lastFireTime = System.currentTimeMillis();
        }
        else
            return;

        final Location muzzle = barrel[barrel.length-1].getCenterPoint().add(0,0.2,0);
        ProjectileFG shell = createProjectile(world,muzzle.getX(), muzzle.getY(), muzzle.getZ(),shooter, this);

        //warning here cause canFire() should check whether the gun has ammo
        if (shell == null) {
            plugin.getLogger().warning(getClass().getName()+": Tried to create projectile but returned null for input: "+getLoadedAmmoType());
            return;
        }


        //make a flash
        FG_VFX.createFlash(muzzle, world);
        FG_VFX.gunFire(muzzle, world);
        FG_VFX.shockwaveParticles(pivot);
        setSmallDistance(0.1);


        canFire = false;
        final List<Player> vibrateFor = getShakenPlayers();
        final Artillery artillery = this;


        new BukkitRunnable()
        {
            boolean shot = false;
            int ticks = 0;

            @Override
            public void run() {

                if (!shot) {
                    shot = true;

                    shell.addToWorld(world);

                    if (requiresReloading())
                        setAmmo(Math.max(0, getAmmo() - 1));

                    ConstructInventory inv = getInventoryGroup().getInventoryByCategory(InventoryCategory.RELOADING);
                    if (inv != null) {
                        inv.updateState();
                    }
                }

                //pivot is already called if there is a rider so don't call pivot twice
                if (getRider() == null) {
                    pivot(aim.getX(), aim.getY());
                }

                FG_VFX.shakeAnimation(vibrateFor,artillery, ticks);

                if (smallBlockDist < SMALL_BLOCK_LENGTH) {
                    ticks ++;
                    //sin(PIx + (Pi/2)) * max(-0.1x + 1, 0)

                    //basically makes the barrel recover faster as it progresses, starting out slow
                    incrementSmallDistance(BARREL_RECOVER_RATE * (Math.min(1,0.000125 * ticks * ticks * ticks)));


                    Location barrelEnd = barrel[barrel.length-1].getCenterPoint();
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
        }.runTaskTimer(FortressGuns.getInstance(), getHeavyFireDelay(), RECOIL_TIME);
    }


    @Override
    public boolean positionSeat() {
        if (rotatingSeat != null) {
            //Math.PI * 1.5 --> 90* angle. Technically it's 270 degrees though
            return posSeatAbsoluteHorizon(rotatingSeat,DIST_X, DIST_Y, getVibrationOffsetY(),Math.PI*1.5);
        }

        return false;
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
    protected void instantiateParts()
    {
        pivot = StandHelper.createCore(this.initialLoc, BODY, aim, world, this);

        //pivot.setRotation(aim);
        rotatingSeat = StandHelper.createInvisiblePart(getSeatLocation(DIST_X, DIST_Y,Math.PI*1.5),SEAT,new EulerAngle(0, aim.getY(),0),world,this);

        //for the base of the artillery
        spawnTurretParts();
        spawnBaseParts();


        calculateOccupiedChunks();
        if (health <= 0)
            setHealth(getMaxHealth());
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
                Location loc = pivot.getLocation().clone().
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
    protected void spawnTurretParts() {

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

            Location centre = pivot.getLocation().clone();


            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            if (small) {
                stand = StandHelper.createInvisiblePart(centre.add(x, height + 0.75, z), BARREL, aim, world, this);
                stand.setSmall(true);
            }
            else {
                stand = StandHelper.createInvisiblePart(centre.add(x, height, z), BODY, aim, world, this);
            }

            barrel[slot] = stand;
        }
    }




    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
        lengthChanged = true;
    }

    protected synchronized void setSmallDistance(double dist) {
        this.smallBlockDist = dist;
        lengthChanged = true;
    }





    protected abstract int getSmallDistThreshold();
}
