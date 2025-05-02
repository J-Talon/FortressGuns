package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.ArtilleryRideable;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Handlers.InteractionHandler;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Artillery.Entities.ArtilleryMaterial;
import me.camm.productions.fortressguns.Artillery.Entities.StandHelper;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MissileLauncher extends ArtilleryRideable {

    boolean fireRight;
    private Entity target;
    private long lastFireTime;



    static final ItemStack BODY, BASE, BARREL;
    static final double RIGHT_ANGLE = Math.PI / 2;
    static final double Y_OFFSET = 0.5;
    static final double HOR_OFFSET = 1;
    static int maxRockets;
    static long cooldown;
    static double maxHealth;


    static final Random RANDOM;



    static {
        BODY = new ItemStack(Material.STONE_BRICKS);
        BASE = new ItemStack(Material.STONE_BRICK_SLAB);
        BARREL = new ItemStack(Material.GREEN_TERRACOTTA);
        RANDOM = new Random();

        maxHealth = 100;
        cooldown = 10000;
        maxRockets = 6;

    }

    private final ArtilleryPart[] stem;

    public MissileLauncher(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        barrel = new ArtilleryPart[6];   ///barrel MUST be a multiple of 2 since we are doing some stuff in spawnTurret() & pivot()
        base = new ArtilleryPart[4][3];
        stem = new ArtilleryPart[2];
        fireRight = true;
        this.target = null;
        lastFireTime = System.currentTimeMillis();
    }

    @Override
    protected void initInventories() {
        interactionInv = new InventoryGroup.BulkPrecision(this);
    }

    public static void setMaxRockets(int maxRockets) {
        MissileLauncher.maxRockets = maxRockets;
    }

    public static void setCooldown(long cooldown) {
        MissileLauncher.cooldown = cooldown;
    }

    public static void setMaxHealth(double maxHealth) {
        MissileLauncher.maxHealth = maxHealth;
    }

    public double getVectorPower() {
        return 2;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    protected @Nullable SimpleMissile createProjectile(net.minecraft.world.level.World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
        SimpleMissile missile = (SimpleMissile) super.createProjectile(world, x, y, z, shooter, source);
        if (missile != null)
            missile.setTarget(target);
        return missile;
    }

    @Override
    public void fire(@Nullable Player shooter) {

        if (!canFire())
            return;

        if (shooter != null) {

            List<ArtilleryPart> parts = getParts();
            target = InteractionHandler.getTarget(shooter.getUniqueId());
            if (target != null) {
                net.minecraft.world.entity.Entity nms = ((CraftEntity)target).getHandle();
                if (nms instanceof ArtilleryPart && parts.contains(nms)) {
                    shooter.sendMessage(ChatColor.RED+"Cannot shoot at self!");
                    return;
                }
            }
            setTarget(target);
        }
        else {
            setTarget(null);
        }

        //
        //frontLeft = barrel[midpoint]
        ArtilleryPart shootingPart, backBlast;

        if (fireRight) {
            shootingPart = barrel[0];
            backBlast = barrel[barrel.length / 2 - 1];
        }
        else {
            shootingPart = barrel[barrel.length / 2];
            backBlast = barrel[barrel.length - 1];
        }

        fireRight = !fireRight;

        Vector dir = eulerToVec(aim).normalize();
        Vector backBlastDir = dir.clone().multiply(-1);

        Vector front = dir.clone().multiply(1.5);  //slightly in front of the thing

        Location spawn = shootingPart.getEyeLocation().add(front);
        Location back = backBlast.getEyeLocation().add(front.multiply(-1));


        net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();
        Artillery construct = this;

        EntityPlayer shooterNMS = shooter == null ? null : ((CraftPlayer)shooter).getHandle();
        SimpleMissile missile = createProjectile(nmsWorld,spawn.getX(), spawn.getY(), spawn.getZ(),shooterNMS,construct);
        if (missile == null) {
            plugin.getLogger().warning(getClass().getName()+": Tried to create projectile but returned null for input: "+getLoadedAmmoType());
            return;
        }

        new BukkitRunnable() {
            public void run() {

                int nextAmmo = getAmmo() - 1;
                if (nextAmmo <= 0) {
                    lastFireTime = System.currentTimeMillis();
                    setAmmo(0);
                }
                else {
                    setAmmo(nextAmmo);
                }

                double vecPow = getVectorPower();

                missile.setMot(new Vec3D(dir.getX() * vecPow, dir.getY() * vecPow, dir.getZ() * vecPow));
                nmsWorld.addEntity(missile);
                world.playSound(spawn, Sound.ITEM_FIRECHARGE_USE,SoundCategory.BLOCKS,2,2);

                //may want to change this to make the stuff more visible
                int ITERS = 20;

                final double HALF = 0.5;
                final double MAG = 0.3;
                do {
                    double x = (RANDOM.nextDouble() - HALF) * MAG, y = (RANDOM.nextDouble() - HALF) * MAG, z = (RANDOM.nextDouble() - HALF) * MAG;
                    x = backBlastDir.getX() + x;
                    y = backBlastDir.getY() + y;
                    z = backBlastDir.getZ() + z;

                    world.spawnParticle(Particle.FLAME,back,0, x,y,z + z,0.4);

                    ITERS --;
                }
                while (ITERS > 0);


                cancel();
            }
        }.runTask(plugin);
    }

    @Override
    protected boolean spawnParts() {
        pivot = StandHelper.createCore(loc.add(0,-0.5,0), BODY, new EulerAngle(0, aim.getY(), 0), world,this);
        rotatingSeat = StandHelper.createInvisiblePart(getSeatLocation(HOR_OFFSET,Y_OFFSET,Math.PI*1.5), ArtilleryMaterial.SEAT.asItem(),new EulerAngle(0,aim.getY(),0),world,this);

        if (pivot == null || !spawnTurretParts() || !spawnBaseParts() )
            return false;

        if (health <= 0)
            setHealth(maxHealth);
        calculateLoadedChunks();

        return true;
    }

    @Override
    protected boolean spawnBaseParts() {

        Location piv = pivot.getLocation(world).subtract(0,0.25,0);
        double rads = 0;
        for (ArtilleryPart[] row: base) {
            for (int slot = 0; slot < row.length; slot++) {
                double[] basePos = getBasePositions(rads);
                Location spawn = piv.clone().add(
                        basePos[0] * LARGE_BLOCK_LENGTH + slot * basePos[0],
                        0,
                        basePos[1] * LARGE_BLOCK_LENGTH + slot * basePos[1]);

                row[slot] = StandHelper.createInvisiblePart(spawn, BASE, null, world, this);
                if (row[slot] == null)
                    return false;
            }
            rads += 2 * Math.PI / 4; //90* offsets
        }
        return true;
    }

    @Override
    protected boolean spawnTurretParts() {
        EulerAngle horizontal = new EulerAngle(0, aim.getY(), 0);


        Location nextStemLoc;
        for (int slot = 0; slot < stem.length; slot ++) {
            nextStemLoc = loc.clone().add(0,(slot+1) * LARGE_BLOCK_LENGTH,0);
            stem[slot] = StandHelper.createInvisiblePart(nextStemLoc, BODY, horizontal,world, this);

            if (stem[slot] == null)
                return false;
        }

        Location[] barrelLocs = getBarrelLocations();
        int midpoint = barrel.length / 2;
        for (int slot = 0; slot < midpoint; slot ++) {
            barrel[slot] = StandHelper.createInvisiblePart(barrelLocs[slot], BARREL, aim,world, this);
            barrel[slot + midpoint] = StandHelper.createInvisiblePart(barrelLocs[slot+midpoint], BARREL, aim, world, this);

            if (barrel[slot] == null || barrel[slot + midpoint] == null)
                return false;
        }

        return true;
    }


    @Override
    public synchronized void pivot(double vertAngle, double horAngle) {


        horAngle = nextHorizontalAngle(aim.getY(), horAngle, horRotSpeed);
        vertAngle = nextVerticalAngle(aim.getX(), vertAngle, vertRotSpeed);

        aim = new EulerAngle(vertAngle, horAngle, 0);

        stem[stem.length-1].setRotation(new EulerAngle(0,horAngle, 0));

        Location[] aimPos = getBarrelLocations();

        int midpoint = barrel.length / 2;
        for (int slot = 0; slot < midpoint; slot ++) {
            barrel[slot].teleport(aimPos[slot]);
            barrel[slot].setRotation(aim);

            barrel[slot + midpoint].teleport(aimPos[slot + midpoint]);
            barrel[slot + midpoint].setRotation(aim);
        }

        positionSeat();
    }

    //basically there's an idea that if a player sits on the seat then they can start locking onto
    //targets
    public synchronized void startTracking() {
     //onions. yummy.
    }




    private Location[] getBarrelLocations() {



        double xStraight, zStraight;
        double yHeight = -LARGE_BLOCK_LENGTH * Math.sin(aim.getX());
        //90* offset

        //yes I know sin(x+0.5pi) = cos(x)
        double horDist = LARGE_BLOCK_LENGTH * Math.cos(aim.getX());

        zStraight = horDist * Math.cos(aim.getY());
        xStraight = -horDist * Math.sin(aim.getY());

        double zRight = horDist * Math.cos(aim.getY() + RIGHT_ANGLE);
        double xRight = -horDist * Math.sin(aim.getY() + RIGHT_ANGLE);

        double horMag = Math.sqrt((zRight * zRight) + (xRight * xRight));

       //this is to prevent the two barrels converging onto each other
        //when the euler angle is near vertical (cause then the horizontal approaches 0)
        //threshold value for horMag is 0.01 since we're div by mag we don't want the values --> infinity
        final double PRESERVATION = 0.75;  //arbitrary artistic choice
        if (horMag < 0.01) {

            //preserves rotation so it doesn't look so weird
            if (xRight < 0)
                xRight = -PRESERVATION;
            else
                xRight = PRESERVATION;


            if (zRight < 0) {
                zRight = -PRESERVATION;
            }
            else zRight = PRESERVATION;


        }
        else {
            zRight = zRight / horMag;
            xRight = xRight / horMag;
        }

        xRight *= LARGE_BLOCK_LENGTH;
        zRight *= LARGE_BLOCK_LENGTH;


        double zLeft = -zRight;
        double xLeft = -xRight;


        int midpoint = barrel.length / 2;
        double distFromMid;

        Location nextStemLoc = stem[stem.length - 1].getLocation(world);
        Location[] barrelLocs = new Location[barrel.length];

        for (int slot = 0; slot < midpoint; slot ++) {
            distFromMid = slot - (barrel.length / 4.0) + 0.5;
            /*
            [][][]...
               |      top view
            [][][]...

            barrel.length / 2 means either the left or the right barrel
            ((barrel.length / 2) / 2) means 1/2 of either the left or right barrel
            we add +0.5 cause we want the midpoint between 2 blocks

             */

            Location spawnRight = nextStemLoc.clone().add(
                    xRight + (xStraight * distFromMid),
                    yHeight * distFromMid,
                    zRight + (zStraight * distFromMid));

            Location spawnLeft = nextStemLoc.clone().add(
                    xLeft + (xStraight * distFromMid),
                    yHeight * distFromMid,
                    zLeft + (zStraight * distFromMid));

            barrelLocs[slot] = spawnRight;
            barrelLocs[slot+midpoint] = spawnLeft;
        }
        return barrelLocs;
    }




    @Override
    public int getMaxAmmo() {
        return maxRockets;
    }


    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.MISSILE == item;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.MISSILE_LAUNCHER;
    }


    @Override
    public boolean canFire() {

        if (getAmmo() > 0 || (!requiresReloading()))
            return true;
        return System.currentTimeMillis() >= (lastFireTime + cooldown);
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = super.getParts();
        parts.addAll(Arrays.asList(stem));
        parts.add(rotatingSeat);
        parts.add(pivot);
        return parts;
    }

    @Override
    public void positionSeat() {
        posSeatAbsoluteHorizon(rotatingSeat,HOR_OFFSET,Y_OFFSET,0,Math.PI*1.5);
    }
}
