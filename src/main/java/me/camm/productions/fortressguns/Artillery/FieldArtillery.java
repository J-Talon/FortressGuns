package me.camm.productions.fortressguns.Artillery;

import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.ModifierType;
import me.camm.productions.fortressguns.Artillery.Projectiles.Shell;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.StandHelper;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;

import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class FieldArtillery extends Artillery
{

    protected ModifierType type;

    public void setType(ModifierType type){
        this.type = type;
    }

    public FieldArtillery(Location loc, World world) {
        super(loc, world);
    }

    @Override
    public synchronized void fire(double power, int recoil, double barrelRecoverRate)
    {
        if (inValid()) {
            remove(false, true);
            return;
        }

        //if it can fire, then fire, else return
        if (canFire())
            lastFireTime = System.currentTimeMillis();
        else
            return;

        //getting the location of the last armorstand in the barrel array
        final Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
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

                    Shell shell = new Shell(EntityTypes.d,muzzle.getX(),muzzle.getY(),muzzle.getZ(),((CraftWorld)world).getHandle(), type);
                    shell.setMot(vector);
                    ((CraftWorld) world).addEntity(shell, CreatureSpawnEvent.SpawnReason.CUSTOM);
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



    public synchronized void unload(){
        remove(dead,false);
    }

    @Override
    public void spawn()
    {
        ItemStack body = new ItemStack(Material.GREEN_TERRACOTTA);
        ItemStack baseClose = new ItemStack(Material.COAL_BLOCK);
        ItemStack baseFar = new ItemStack(Material.STONE_BRICK_SLAB);
        ItemStack barrelMat = new ItemStack(Material.DISPENSER);

        //World world, Artillery body, double d0, double d1, double d2
        pivot = StandHelper.getCore(loc, body, aim, world, this);


        for (int slot=0;slot< barrel.length;slot++)
        {
            boolean small = false;
            double totalDistance;

            if (slot>=1) {
                totalDistance = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);
                  small = true;
            }
            else
                totalDistance = (slot+1)* LARGE_BLOCK_LENGTH;


            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            if (small) {
                stand = StandHelper.spawnPart(centre.add(x, height + 0.75, z), barrelMat, aim, world, this);
                stand.setSmall(true);
            }
            else
                stand = StandHelper.spawnPart(centre.add(x, height, z),body,aim,world,this);


            barrel[slot] = stand;

        }


        double rads = -Math.PI/3;
        int bar = 0;


        //for the base of the artillery
        for (ArtilleryPart[] standRow: base) {
            double[] position = getBasePositions(rads);  //get the x, z values for the base

            int length = 0;



            for (int slot=0;slot<standRow.length;slot++) {
                Location loc = pivot.getLocation(this.world).clone().add(position[0] + (length*position[0]), -0.75, position[1]+(length*position[1]));

                //(World world, Artillery body, double d0, double d1, double d2)
                ArtilleryPart part;

                //if the length is close to base, then give it wheels, else give it
                //supports
                if (length >=1)
                    part = StandHelper.spawnPart(loc,baseFar,aim,world,this);
                else if (bar!=base.length-1)
                    part = StandHelper.spawnPart(loc,baseClose,aim,world, this);
                else
                    part = StandHelper.spawnPart(loc,body,aim,world, this);

                length ++;
                standRow[slot] = part;
            }
            bar ++;

            if (bar == base.length-1)
                rads = Math.PI; //so that it is directly behind the cannon (facing south)
            else
                rads += 2 * Math.PI / 3;
        }

    }



    @Override
    public boolean canFire() {
        return canFire;
    }
}
