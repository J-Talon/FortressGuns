package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MissileLauncher extends Artillery {

    boolean fireRight;

    static final ItemStack BODY, BASE, BARREL;
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
        base = new ArtilleryPart[3][4];
        stem = new ArtilleryPart[1];
        fireRight = true;
    }

    @Override
    public void fire(@Nullable Player shooter) {
        Location muzzleLeft, muzzleRight;

    }




    @Override
    protected void spawnParts() {
        pivot = StandHelper.getCore(loc.add(0,-0.5,0), BODY, new EulerAngle(0, aim.getY(), 0), world,this);
        spawnTurretParts();
        spawnBaseParts();

        if (health <= 0)
            setHealth(HEALTH);
        calculateLoadedChunks();
    }

    @Override
    protected void spawnBaseParts() {

        Location piv = pivot.getLocation(world).subtract(0,0.25,0);
        double rads = 0;
        for (ArtilleryPart[] row: base) {
            for (int slot = 0; slot < row.length; slot++) {
                double[] basePos = getBasePositions(rads);
                Location spawn = piv.clone().add(
                        basePos[0] * LARGE_BLOCK_LENGTH + slot * basePos[0],
                        0,
                        basePos[1] * LARGE_BLOCK_LENGTH + slot * basePos[1]);

                row[slot] = StandHelper.spawnPart(spawn, BASE, null, world, this);
            }
            rads += 2 * Math.PI / 3;
        }
    }

    @Override
    protected void spawnTurretParts() {
        EulerAngle horizontal = new EulerAngle(0, aim.getY(), 0);

        Location nextStemLoc;
        for (int slot = 0; slot < stem.length; slot ++) {
            nextStemLoc = loc.clone().add(0,(slot+1) * LARGE_BLOCK_LENGTH,0);
            stem[slot] = StandHelper.spawnPart(nextStemLoc, BODY, horizontal,world, this);
        }

        Location[] barrelLocs = getBarrelLocations();
        int midpoint = barrel.length / 2;
        for (int slot = 0; slot < midpoint; slot ++) {
            barrel[slot] = StandHelper.spawnPart(barrelLocs[slot], BARREL, aim,world, this);
            barrel[slot + midpoint] = StandHelper.spawnPart(barrelLocs[slot+midpoint], BARREL, aim, world, this);
        }
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
            barrel[slot + midpoint].teleport(aimPos[slot + midpoint]);
        }

        super.pivot(vertAngle, horAngle);
    }



    private Location[] getBarrelLocations() {
        Location nextStemLoc = stem[stem.length - 1].getLocation(world);

        double xStraight, zStraight;
        double yHeight = -LARGE_BLOCK_LENGTH * Math.sin(aim.getX());
        //90* offset
        final double RIGHT_ANGLE = Math.PI / 2;
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

    @Override
    public boolean canFire() {
        return false;
    }

    @Override
    public double getMaxHealth() {
        return 35;
    }

    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = super.getParts();
        parts.addAll(Arrays.asList(stem));
        parts.add(pivot);
        return parts;
    }

    @Override
    protected void positionSeat() {


    }
}
