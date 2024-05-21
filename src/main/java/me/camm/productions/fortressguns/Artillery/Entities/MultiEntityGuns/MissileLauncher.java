package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.SideSeated;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.SimpleMissile;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.List;


public class MissileLauncher extends Artillery implements SideSeated {

    boolean fireRight;


    static final ItemStack BODY, BASE, BARREL;
    static final double RIGHT_ANGLE = Math.PI / 2;
    static final double Y_OFFSET = 0.5;


    static final int HEALTH;
    static {
        BODY = new ItemStack(Material.STONE_BRICKS);
        BASE = new ItemStack(Material.STONE_BRICK_SLAB);
        BARREL = new ItemStack(Material.GREEN_TERRACOTTA);
        HEALTH = 35;


    }


    private final ArtilleryPart[] stem;

    public MissileLauncher(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        barrel = new ArtilleryPart[6];   ///barrel MUST be a multiple of 2 since we are doing some stuff in spawnTurret() & pivot()
        base = new ArtilleryPart[3][3];
        stem = new ArtilleryPart[2];
        fireRight = true;
    }

    @Override
    public void fire(@Nullable Player shooter) {


        if (!canFire())
            return;

        //unfinished
        //note that frontLeft = barrel[midpoint]
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
        Vector front = dir.clone().multiply(3);

        Location spawn = shootingPart.getEyeLocation().add(front);
        Location back = backBlast.getEyeLocation().add(front.multiply(-1));

        Vector backBlastDir = back.clone().subtract(spawn).toVector();

        net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();
        dir.multiply(0.5);
        Construct construct = this;

        new BukkitRunnable() {
            public void run() {
                SimpleMissile missile = new SimpleMissile(EntityTypes.d, spawn.getX(), spawn.getY(), spawn.getZ(), nmsWorld, shooter,construct);
                missile.setMot(new Vec3D(dir.getX(), dir.getY(), dir.getZ()));
                nmsWorld.addEntity(missile);

                int iters = 3;
                do {
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,back.add(backBlastDir),0,
                            backBlastDir.getX(),backBlastDir.getY(),backBlastDir.getZ(),0.1 + (iters * 0.1));
                    iters --;
                }
                while (iters > 0);


                cancel();
            }
        }.runTask(plugin);
    }




    @Override
    protected boolean spawnParts() {
        pivot = StandHelper.createCore(loc.add(0,-0.5,0), BODY, new EulerAngle(0, aim.getY(), 0), world,this);
        rotatingSeat = StandHelper.createInvisiblePart(getSeatSpawnLocation(this, Y_OFFSET), ArtilleryMaterial.SEAT.asItem(),new EulerAngle(0,aim.getY(),0),world,this);

        if (pivot == null || !spawnTurretParts() || !spawnBaseParts() )
            return false;

        if (health <= 0)
            setHealth(HEALTH);
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
            rads += 2 * Math.PI / 3;
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

    //basically if a player sits on the seat then they can start locking onto
    //targets
    public synchronized void startTracking() {

    }




    private Location[] getBarrelLocations() {



        Location nextStemLoc = stem[stem.length - 1].getLocation(world);

        double xStraight, zStraight;
        double yHeight = -LARGE_BLOCK_LENGTH * Math.sin(aim.getX());
        //90* offset

        double horDist = LARGE_BLOCK_LENGTH * Math.cos(aim.getX());

        zStraight = horDist * Math.cos(aim.getY());
        xStraight = -horDist * Math.sin(aim.getY());

        double zRight = horDist * Math.cos(aim.getY() + RIGHT_ANGLE);
        double xRight = -horDist * Math.sin(aim.getY() + RIGHT_ANGLE);

        double zLeft = horDist * Math.cos(aim.getY() - RIGHT_ANGLE);
        double xLeft = -horDist * Math.sin(aim.getY() - RIGHT_ANGLE);

        int midpoint = barrel.length / 2;
        double distFromMid;

        Location[] barrelLocs = new Location[barrel.length];
        for (int slot = 0; slot < midpoint; slot ++) {
            distFromMid = slot - (barrel.length / 4.0) + 0.5;

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
    public ArtilleryType getType() {
        return ArtilleryType.MISSILE_LAUNCHER;
    }


    //not done
    @Override
    public boolean canFire() {
        return true;
    }

    @Override
    public double getMaxHealth() {
        return 35;
    }

    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = super.getParts();
        parts.addAll(Arrays.asList(stem));
        parts.add(rotatingSeat);
        parts.add(pivot);
        return parts;
    }

    @Override
    protected void positionSeat() {
        positionSeat(rotatingSeat, this, Y_OFFSET);
    }
}
