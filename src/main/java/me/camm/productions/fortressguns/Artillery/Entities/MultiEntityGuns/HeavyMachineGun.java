package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;

import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
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

    }

    public ArtilleryPart getRotatingSeat(){
        return rotatingSeat;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return loadingInventory.getInventory();
    }


    private void fireBurst(){
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
                    fireSingleShot();

                    if (reference == shots)
                        canFire = true;

                    cancel();


                }
            }.runTaskLater(plugin, iterations * delayTicks);
        }
    }


    private void fireSingleShot() {


        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();
        if (passengers.isEmpty())
            return;

        Entity possibleOperator = passengers.get(0);
        Player operator = possibleOperator instanceof Player ? (Player) possibleOperator : null;
        if (operator == null)
            return;


        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);


        createFlash(muzzle);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,1f,2f);


        projectileVelocity = eulerToVec(aim).normalize();
        Vector direction = projectileVelocity.clone();
        Vector origin = muzzle.toVector();

        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();


        //Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter
        RayTraceResult result = world.rayTrace(
                muzzle,
                projectileVelocity,
                RANGE,
                FluidCollisionMode.ALWAYS,
                true,
                0.1,
                new PredicateEqual(operator,this));


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

            final double distance = hitPosCopy != null ? hitPosCopy.distance(muzzle) : RANGE;
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
      fireBurst();
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

}




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
