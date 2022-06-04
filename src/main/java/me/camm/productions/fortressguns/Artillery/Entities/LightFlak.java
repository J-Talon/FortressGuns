package me.camm.productions.fortressguns.Artillery.Entities;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightFlakShell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.List;

/**
 * @author CAMM
 */
public class LightFlak extends HeavyMachineGun {

    private final static ItemStack BARREL;
    //(double power, int recoilTime, double barrelRecoverRate)

    private final static double POWER, BARREL_RECOVER, RECOIL;




    static {
        BARREL = new ItemStack(Material.RED_TERRACOTTA);
        POWER = 4;
        BARREL_RECOVER = 0.1;

        RECOIL = 0.3;


    }

    public LightFlak(Location loc, World world, ChunkLoader loader) {
        super(loc, world, loader);
    }




    @Override
    public void spawn(){

        dead = false;
        loaded = true;
        world.playSound(loc,Sound.BLOCK_ANVIL_DESTROY,0.5f,1);

        ArtilleryPart support;
        pivot = StandHelper.getCore(loc,BARREL,aim,world, this);
        support = StandHelper.spawnPart(loc.clone().subtract(0,0.5,0),null,aim, world,this);
        support.setInvisible(false);

        base[0][0] = support;

        Location rotatingSeat = support.getLocation(world);

        double rotSeatZ = -Math.cos(aim.getY());
        double rotSeatX = Math.sin(aim.getY());

        rotatingSeat.add(rotSeatX,0.5,rotSeatZ);
        this.rotatingSeat = StandHelper.spawnPart(rotatingSeat,SEAT,aim,world,this);

        support.setArms(true);
        support.setPose(rightArm, leftArm, body, rightLeg, leftLeg);
        support.setBasePlate(false);


        boolean down = false;
        for (int slot = 0;slot < barrel.length;slot++) {

            if (slot >= 2)
                down = true;

            double totalDistance = (0.5 * LARGE_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), MUZZLE, aim, world, this);
            stand.setSmall(true);

            if (down)
                stand.setFacesDown(true);

            barrel[slot] = stand;
        }

        initLoadedChunks();
        if (health <= 0)
            setHealth(HEALTH);
        // pivot(0,0);
    }

    @Override
    public void fire(double power, int recoilTime, double barrelRecoverRate) {
        this.fire();
    }

    public synchronized void incrementSmallDistance(double increment){
        this.currentSmallLength += increment;

    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FLAK_LIGHT;
    }

    @Override
    public void fire(){

        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();


        Player operator = null;
        if (passengers.size() == 0)
            return;

        Entity e = passengers.get(0);
        if (e instanceof Player)
            operator = (Player)e;

        if (operator == null)
            return;


        if (canFire()) {
            lastFireTime = System.currentTimeMillis();
            canFire = false;
        }
        else
            return;

        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
        Block block = muzzle.getBlock();
        Material mat = block.getType();

        final boolean flashed;
        if (isFlashable(block)) {
            block.setType(Material.LIGHT);
            flashed = true;
        }
        else
            flashed = false;

        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        projectileVelocity.setX(x);
        projectileVelocity.setY(y);
        projectileVelocity.setZ(z);
        projectileVelocity.normalize().multiply(POWER);


        net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();

        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();

        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),10,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,SoundCategory.BLOCKS,1f,2f);

        LightFlakShell shell = new LightFlakShell(nmsWorld,muzzle.getX(),muzzle.getY(),muzzle.getZ(),nmsOperator);

        new BukkitRunnable() {

            boolean spent = false;
            public void run() {

                if (!spent) {
                    currentSmallLength = RECOIL;
                    spent = true;
                    shell.setMot(projectileVelocity.getX(), projectileVelocity.getY(), projectileVelocity.getZ());
                    nmsWorld.addEntity(shell);
                    if (flashed)
                    block.setType(mat);
                }

                //pivot
                if (currentSmallLength < SMALL_BLOCK_LENGTH) {
                   incrementSmallDistance(BARREL_RECOVER);
                }
                else {
                    currentSmallLength = SMALL_BLOCK_LENGTH;
                    cancel();
                    canFire = true;
                }

            }
        }.runTaskTimer(FortressGuns.getInstance(),1,1);



    }
}
