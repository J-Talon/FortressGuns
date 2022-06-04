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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class FieldArtillery extends Artillery
{

    protected ModifierType type;

    public void setType(ModifierType type){
        this.type = type;
    }

    public FieldArtillery(Location loc, World world, ChunkLoader loader) {
        super(loc, world, loader);
    }


    @Override
    public synchronized void fire(double power, int recoil, double barrelRecoverRate)
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

        //if it can fire, then fire, else return
      //  if (canFire())
     //       lastFireTime = System.currentTimeMillis();
     //   else
         //   return;


        //getting the location of the last armorstand in the barrel array
        final Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
        Block block = muzzle.getBlock();

        //make a flash
        final Material mat = block.getType();
        final boolean flashed;
        if (isFlashable(block)) {
            block.setType(Material.LIGHT);
            flashed = true;
        }
        else
            flashed = false;

        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),30,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);
        world.playSound(muzzle, Sound.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,2,0.2f);


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

                    Shell shell = new Shell(EntityTypes.d,muzzle.getX(),muzzle.getY(),muzzle.getZ(),((CraftWorld)world).getHandle(), type);
                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);

                    if (flashed)
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


    protected synchronized void incrementSmallDistance(double increment){
        this.currentSmallLength += increment;
    }


}
