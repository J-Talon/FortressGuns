package me.camm.productions.fortressguns.Artillery;

import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.ModifierType;
import me.camm.productions.fortressguns.Artillery.Projectiles.Shell;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public abstract class FlakArtillery extends Artillery
{
    protected volatile Entity target;


    public FlakArtillery(Location loc, World world) {
        super(loc, world);
        this.target = null;
    }

    public synchronized void setTarget(Entity target){
        this.target = target;
    }

    public void seat(EntityHuman human){
        pivot.seat(human);
    }

    protected synchronized void incrementSmallDistance(double increment){
        this.currentSmallLength += increment;
    }

    @Override
    public void fire(double power, int recoil, double barrelRecoverRate) {

        if (inValid()) {
            remove(false, true);
            return;
        }

        final Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);

        if (target == null || target.isRemoved() || !target.isAlive())
            target = null;

        //if it can fire, then fire, else return
        if (canFire())
            lastFireTime = System.currentTimeMillis();
        else
            return;

        //getting the location of the last armorstand in the barrel array, which is the muzzle
        Block block = muzzle.getBlock();

        //make a flash
        final Material mat = block.getType();
        if (isFlashable(block)) {
            block.setType(Material.LIGHT);
        }

        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),30,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);
        world.playSound(muzzle, Sound.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,2,0.2f);


        //getting the values for the projectile velocity.
        //tan and sine are (-) since MC's grid is inverted
        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        Vector velocity = new Vector(x,y,z).normalize();

        x = velocity.getX()*power;
        y = velocity.getY()*power;
        z = velocity.getZ()*power;

        final Vec3D vector = new Vec3D(x,y,z);


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

                        Shell shell = new Shell(EntityTypes.d, muzzle.getX(), muzzle.getY(), muzzle.getZ(), ((CraftWorld) world).getHandle(), ModifierType.FLAK);
                        shell.setMot(vector);
                        shell.setTerminus(target);
                        ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);
                        shell.flyFlak();
                        block.setType(mat);

                }


                if (currentSmallLength < SMALL_BLOCK_LENGTH) {
                    pivot(aim.getX(), aim.getY());
                    incrementSmallDistance(barrelRecoverRate);
                    Location loc = barrel[barrel.length-1].getEyeLocation();
                    world.spawnParticle(Particle.SMOKE_NORMAL,loc,5,0,0.1,0,0.3);
                }
                else
                {
                    currentSmallLength = SMALL_BLOCK_LENGTH;
                    pivot(aim.getX(), aim.getY());
                    canFire = true;
                    cancel();
                }

            }
        }.runTaskTimer(FortressGuns.getInstance(), 3, recoil);



    }


    public void aimAtTarget(){

        System.out.println("attempt aim");



        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
        if (target == null || target.isRemoved() || !target.isAlive()) {
            target = null;
            return;
        }



        double deltaX = target.locX() - muzzle.getX();

        ///this is getting the height of where the arrow should be shot at
        //av.c = target y
        //getHeight = how tall the target is
           /*
           So by mult by 0.3~, then we are targeting centre mass.
            */

        double deltaY = target.e(0.3333333333333333D) - muzzle.getY();  // return this.av.c + (double)this.getHeight() * d0;
        double deltaZ = target.locZ() - muzzle.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        Vec3D vector = new Vec3D(deltaX,deltaY + distance * 0.20000000298023224D,deltaZ);
        //I don't know what the 0.2~ is about

        double vectorMagnitude = vector.h();
        double yRotation = (float)(MathHelper.d(vector.b, vector.d));
        double xRotation = (float)(MathHelper.d(vector.c, vectorMagnitude));
        //this is making the arrow look backwards, so get rid of the 57.29
        //57.29 ~= 1 rad in deg

        System.out.println("pivoting");
        //we're pivoting since we're on axe planes (think of pitch, yaw on an airplane)
        pivot(-xRotation,-yRotation);
    }
}
