package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;

import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import net.minecraft.core.Vector3f;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
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
        final int shots = 2;  //this is intentional
                               // interact time is ~5 ticks, so 4 ticks waiting (2*2) + 1
                               // tick to receive next interaction --> the action is fluid under normal conditions

        if (!canFire())
            return;

        canFire = false;
        while (iterations < shots) {

            iterations ++;
            final int reference = iterations;
            new BukkitRunnable() {
                public void run() {

                    Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
                    fireSingleShot(muzzle);

                    Vector origin = muzzle.toVector();
                    Location pivLoc = pivot.getLocation(world);
                    Item item = world.dropItem(pivLoc,CASING);
                    Vector vel = origin.clone().normalize();
                    double x = vel.getX();
                    double z = vel.getZ();
                    vel.setX(z);
                    vel.setZ(x);

                    item.setVelocity(vel);

                    if (reference >= shots)
                        canFire = true;

                    cancel();
                }
            }.runTaskLater(plugin, iterations * delayTicks);
        }
    }


    private void fireSingleShot(Location muzzle) {


        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();
        if (passengers.isEmpty())
            return;

        Entity possibleOperator = passengers.get(0);
        Player operator = possibleOperator instanceof Player ? (Player) possibleOperator : null;
        if (operator == null)
            return;


        createFlash(muzzle);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,1f,2f);


        projectileVelocity = eulerToVec(aim).normalize();
        Vector direction = projectileVelocity.clone().multiply(vectorPower);

        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();
        net.minecraft.world.level.World nms = ((CraftWorld)world).getHandle();

        StandardLightShell shell = new StandardLightShell(nms,muzzle.getX(), muzzle.getY(), muzzle.getZ(),nmsOperator,this);
        shell.setMot(direction.getX(), direction.getY(), direction.getZ());
        nms.addEntity(shell);

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
