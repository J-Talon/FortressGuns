package me.camm.productions.fortressguns.Artillery.Entities.Abstract;


import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.ModifierType;
import me.camm.productions.fortressguns.Artillery.Projectiles.Shell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public abstract class FieldArtillery extends Artillery
{

    protected ModifierType type;

    public void setType(ModifierType type){
        this.type = type;
    }

    public FieldArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }


    @Override
    public synchronized void fire(double power, int recoil, double barrelRecoverRate, @Nullable Player shooter)
    {
        if (inValid()) {
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

        Vector velocity = new Vector(x,y,z).normalize().multiply(power);
        final Vec3D vector = new Vec3D(velocity.getX(),velocity.getY(), velocity.getZ());


        currentSmallLength = 0;
        pivot(aim.getX(), aim.getY());
        canFire = false;

        new BukkitRunnable()
        {
            boolean shot = false;

            @Override
            public void run() {


                if (!shot) {
                    shot = true;

                    Shell shell = new Shell(EntityTypes.d,muzzle.getX(),muzzle.getY(),muzzle.getZ(),((CraftWorld)world).getHandle(), type, shooter);
                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);

                }

                pivot(aim.getX(), aim.getY());
                if (currentSmallLength < SMALL_BLOCK_LENGTH) {
                    incrementSmallDistance(barrelRecoverRate);
                    Location barrelEnd = barrel[barrel.length-1].getEyeLocation();
                    world.spawnParticle(Particle.SMOKE_NORMAL,barrelEnd,5,0,0.1,0,0.3);
                }
                else
                {
                    currentSmallLength = SMALL_BLOCK_LENGTH;
                    canFire = true;
                    cancel();
                }

            }
        }.runTaskTimer(FortressGuns.getInstance(), 4, recoil);

    }


    protected synchronized void incrementSmallDistance(double increment){
        this.currentSmallLength += increment;
    }


}
