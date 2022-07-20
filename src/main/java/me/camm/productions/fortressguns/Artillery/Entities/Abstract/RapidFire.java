package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import me.camm.productions.fortressguns.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/*
 * @author CAMM
 */
public abstract class RapidFire extends Artillery {


    protected Vector projectileVelocity;
    protected static ItemStack CASING;

    protected ArtilleryPart rotatingSeat;

    protected FireTrigger triggerHandle;

    static {
        CASING = new ItemStack(Material.IRON_NUGGET);
        if (CASING.getItemMeta() != null) {
            ItemMeta meta = CASING.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Bullet Casing");
            CASING.setItemMeta(meta);

        }
    }




    public RapidFire(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
        projectileVelocity = new Vector(0,0,0);
        loc.add(0,1,0);
    }


    public ArtilleryPart getRotatingSeat() {
        return rotatingSeat;
    }

    public abstract double getRange();

    @Override
    public void fire(double power, int recoilTime, double barrelRecoverRate, @Nullable Player shooter) {
        fire(null);
    }


    @Override
    public synchronized void fire(@Nullable Player shooter) {

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


        if (canFire()) {
            lastFireTime = System.currentTimeMillis();
            canFire = false;
        }
        else
            return;

        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);


        createFlash(muzzle);

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

        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),30,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,1f,2f);

        //Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter
        RayTraceResult result = world.rayTrace(muzzle,projectileVelocity,getRange(), FluidCollisionMode.ALWAYS,true, 0.1,new PredicateEqual(operator,this));


        currentSmallLength -= 0.05;

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

                if (hardness < Material.DIRT.getHardness())
                    hitBlock.breakNaturally();
            }


            if (hit != null) {
                net.minecraft.world.entity.Entity nms = ((CraftEntity) hit).getHandle();
                nms.damageEntity(GunSource.gunShot(nmsOperator), 28);

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
                Vector vel = origin.clone().multiply(0.2);
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

                currentSmallLength = SMALL_BLOCK_LENGTH;
            }
        }.runTaskLater(FortressGuns.getInstance(),1);



    }
}
