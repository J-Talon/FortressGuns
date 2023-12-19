package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.HeavyArtillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.FlakShell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
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



    private double[] debug = {0,0};

    public void autoAim() {
        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);

        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            aiming = false;
            return;
        }

       Location target = this.target.getBukkitEntity().getLocation();
       EulerAngle aim = StandHelper.getLookatRotation(muzzle, target);

       EulerAngle currentAim = this.getAim();
       double dotProd = currentAim.getX() * debug[0] + currentAim.getY() * debug[1];
       if (dotProd < 0.5) {
           System.out.println(dotProd);
        //   System.out.println("target Loc: "+target+"|| \nmuzzle loc: "+muzzle+"\n\n");
       }

       pivot(-aim.getX(), -aim.getY());  //we got the rotation from the target to the muzzle. *-1 reverses it.

       debug[0] = -aim.getX();
       debug[1] = -aim.getY();
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

    /*

  Locations: the locations which an entity has been tracked for
  period: the period between each tracking time, in seconds
   */ public boolean aimMoving(Location[] locations, int period){

        double threshold = 5;
        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);
        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            return false;
        }

        Location targetLocation = new Location(world, target.locX(), target.locY(), target.locZ());

        Vector vAverage = new Vector(0,0,0);

        if (locations.length <= 1)
            return false;

        for (int slot = 0;slot< locations.length-1;slot++) {
            Location current = locations[slot];
            Location next = locations[slot+1];

            next.clone().subtract(current);
            vAverage.add(next.toVector());

        }

        vAverage.multiply(1/period);

        if (vAverage.clone().subtract(target.getBukkitEntity().getVelocity()).lengthSquared() > threshold*threshold) {
            autoAim();
            return true;
        }


        double distToTarget = muzzle.distance(targetLocation);

        if (distToTarget == 0) {
            autoAim();
            return true;
        }

        //time in seconds
        double timeToTarget = (vectorPower * 20)/distToTarget;

        Vector targVelocity = target.getBukkitEntity().getVelocity();
        targVelocity.clone().multiply(timeToTarget);

        Location predicted = targVelocity.toLocation(world).add(targetLocation);
        double deltaX = predicted.getX() - muzzle.getX();
        double deltaZ = predicted.getZ() - muzzle.getZ();
        double deltaY = predicted.getY() - muzzle.getY();


        double horAngle = deltaZ == 0 ? 0 : Math.atan(deltaX/deltaZ);
        double horDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double vertAngle = horDistance == 0 ? 90: Math.atan(deltaY/horDistance);


        horAngle = Math.toRadians(horAngle);
        vertAngle = Math.toRadians(vertAngle);

        pivot(vertAngle, horAngle);

        return true;
    }


}
