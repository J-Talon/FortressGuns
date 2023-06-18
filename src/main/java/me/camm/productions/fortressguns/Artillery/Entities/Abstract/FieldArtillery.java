package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Projectiles.StandardShell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import javax.annotation.Nullable;

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

        //getting the values for the projectile velocity.
        //tan and sine are (-) since MC's grid is inverted
        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        Vector velocity = new Vector(x,y,z).normalize().multiply(vectorPower);
        final Vec3D vector = new Vec3D(velocity.getX(),velocity.getY(), velocity.getZ());

        smallBlockDist = 0;
        canFire = false;

        new BukkitRunnable()
        {
            boolean shot = false;
            @Override
            public void run() {

                if (!shot) {
                    shot = true;
                    StandardShell shell = new StandardShell(EntityTypes.d,muzzle.getX(),muzzle.getY(),muzzle.getZ(),((CraftWorld)world).getHandle(), shooter);
                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

                if (!getHasRider()) {
                    pivot(aim.getX(), aim.getY());
                }

                if (smallBlockDist < SMALL_BLOCK_LENGTH) {
                    incrementSmallDistance(barrelRecoverRate);
                    Location barrelEnd = barrel[barrel.length-1].getEyeLocation();
                    world.spawnParticle(Particle.SMOKE_NORMAL,barrelEnd,5,0,0.1,0,0.3);
                }
                else
                {
                    smallBlockDist = SMALL_BLOCK_LENGTH;
                    canFire = true;
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(), 4, recoilTime);

    }


    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
    }


}
