package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyArtillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.FlakShell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


public abstract class FlakArtillery extends HeavyArtillery
{

    protected Entity target;
    protected boolean aiming;

    //variables for aiming based on average v

    /*
This method is called in a loop. You can think of it as being called many times per second
 */

   /*
   Constructor.
    */
    public FlakArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader, aim);
        this.target = null;
        aiming = false;
    }


    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
    }

    public void fire(@Nullable Player shooter) {

        if (isInvalid()) {
            remove(false, true);
            return;
        }

        final Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);

        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            aiming = false;
        }

        //if it can fire, then fire, else return
        if (canFire())
            lastFireTime = System.currentTimeMillis();
        else
            return;

        createFlash(muzzle);
        createShotParticles(muzzle);

        //getting the values for the projectile velocity.
        //tan and sine are (-) since MC's grid is inverted
        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        Vector velocity = new Vector(x,y,z).normalize();

        x = velocity.getX()*vectorPower;
        y = velocity.getY()*vectorPower;
        z = velocity.getZ()*vectorPower;

        final Vec3D vector = new Vec3D(x,y,z);
        smallBlockDist = 0;
        canFire = false;

        new BukkitRunnable()
        {
            boolean shot = false;

            @Override
            public void run() {

                if (!shot) {
                    shot = true;

                        FlakShell shell = new FlakShell(EntityTypes.d, muzzle.getX(), muzzle.getY(), muzzle.getZ(), ((CraftWorld) world).getHandle(), shooter);
                        shell.setMot(vector);
                        shell.setTerminus(target);
                        ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

                if (!hasRider) {
                    pivot(aim.getX(), aim.getY());
                }

                if (smallBlockDist < SMALL_BLOCK_LENGTH) {
                    incrementSmallDistance(barrelRecoverRate);
                    Location loc = barrel[barrel.length-1].getEyeLocation();
                    world.spawnParticle(Particle.SMOKE_NORMAL,loc,5,0,0.1,0,0.3);
                }
                else
                {
                    smallBlockDist = SMALL_BLOCK_LENGTH;
                    canFire = true;
                    cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(), 3, recoilTime);
    }



    public void aimStatic() {
        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);

        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            aiming = false;
            return;
        }

       Location target = this.target.getBukkitEntity().getLocation();



        Location piv = getPivot().getLocation(world);
        double targDist = target.distanceSquared(piv);
        double barrelDist = muzzle.distanceSquared(piv);

        if (targDist <= barrelDist)
            return;


       EulerAngle aim = StandHelper.getLookatRotation(muzzle, target);

       pivot(-aim.getX(), -aim.getY());  //we got the rotation from the target to the muzzle. *-1 reverses it.

    }


    public Entity getTarget(){
        return this.target;
    }


    public boolean setTarget(Entity target){
        if (target instanceof ArtilleryPart && this.getParts().contains(target)) {
            return false;
        }

        this.target = target;
        return true;
    }

    public void aimMoving(){


    }


}
