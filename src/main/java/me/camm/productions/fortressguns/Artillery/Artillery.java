package me.camm.productions.fortressguns.Artillery;

import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemCreator;
import me.camm.productions.fortressguns.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.ExplosionEffect;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class Artillery {

    protected Plugin plugin;
    protected ArtilleryPart[] barrel;
    protected ArtilleryPart[][] base;
    protected ArtilleryCore pivot;
    protected EulerAngle aim;//


    protected volatile double health;//--

    private final Set<Chunk> loaders;//

    protected Location loc; //
    protected World world;//

    protected boolean dead;//

    protected final static double LARGE_BLOCK_LENGTH = 0.6;
    protected final static double SMALL_BLOCK_LENGTH = 0.4;

    protected volatile double currentLargeLength;//
    protected volatile double currentSmallLength;//
    protected volatile boolean canFire;

    protected long lastFireTime;//

    private enum DamageMultiplier{
        EXPLOSION(3),
        FIRE(1.5),
        GUN(0.9),
        MAGIC(0.01),
        DEFAULT(0.3);

        final double multiplier;

        DamageMultiplier(double mult){
            this.multiplier = mult;
        }
    }



    public Artillery(Location loc, World world) {

        this.plugin = FortressGuns.getInstance();
        this.loc = loc;
        this.world = world;
        this.lastFireTime = System.currentTimeMillis();

        this.loaders = new HashSet<>();
        currentLargeLength = LARGE_BLOCK_LENGTH;
        currentSmallLength = SMALL_BLOCK_LENGTH;
        health = 0;
        dead = false;
        aim = new EulerAngle(0,0,0);
        this.canFire = true;
    }

    public synchronized void pivot(double vertAngle, double horAngle)
    {
        if (inValid())
            remove(false, true);


        EulerAngle barrelAngle = null;
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            if (stand.isSmall())
                totalDistance = (currentLargeLength*0.75 + 0.5*currentSmallLength) + (slot * currentSmallLength);
            else
                totalDistance = (slot+1)*currentLargeLength;


            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);
            double horizontalDistance = totalDistance*Math.cos(vertAngle);


            //x and z distances relative to the pivot
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);


            if (barrelAngle == null) {
                barrelAngle = new EulerAngle(vertAngle,horAngle,0);
            }

            StandHelper.setRotation(stand, barrelAngle);
            //   stand.setHeadPose(barrelAngle);

            Location centre = pivot.getLocation(world).clone();

            if (stand.isSmall()) {
                Location teleport = centre.add(x, height + 0.75, z);
                StandHelper.teleport(stand,teleport.getX(),teleport.getY(),teleport.getZ());

            }
            else {
                Location teleport = centre.clone().add(x, height, z);
                StandHelper.teleport(stand,teleport.getX(),teleport.getY(),teleport.getZ());

                // stand.teleport(centre.clone().add(x, height, z));
            }
        }

        if (barrelAngle!=null) {
            StandHelper.setRotation(pivot, barrelAngle);
            aim = barrelAngle;
        }
    }

    public double[] getBasePositions(double radian){
        double distance = LARGE_BLOCK_LENGTH;
        double z = distance*Math.cos(radian);
        double x = -distance*Math.sin(radian);
        return new double[]{x,z};
    }


    public abstract void fire(double power, int recoilTime,double barrelRecoverRate);
    public abstract void fire();
    public abstract void spawn();
    public abstract ArtilleryType getType();
    public abstract boolean canFire();
    public abstract double getMaxHealth();

    public final boolean inValid(){
        return (pivot == null || (!pivot.isAlive()) || health <= 0 || dead);
    }

    public final synchronized void remove(boolean dropItem, boolean exploded)
    {
        ArrayList<EntityArmorStand> components = new ArrayList<>(Arrays.asList(barrel));
        Arrays.stream(base).forEach(armorstandList -> components.addAll(Arrays.asList(armorstandList)));
        components.add(pivot);

        Location loc = pivot.getLocation(world).clone();

        if (exploded) {
            ExplosionEffect.explodeArtillery(loc, world);
        }

        if (dropItem) {
            ArtilleryItemCreator.packageArtillery(this);
        }



        new BukkitRunnable()
        {
            @Override
            public void run() {
                components.forEach(Entity::die);
                cancel();
            }
        }.runTask(plugin);

    }

    public ArmorStand getPivot() {
        return (ArmorStand)pivot.getBukkitEntity();
    }

    public static boolean isFlashable(Block block) {
        Material type = block.getType();
        switch (type) {
            case AIR:
            case CAVE_AIR:
            case VOID_AIR:
                return true;
        }
        return false;
    }

    public World getWorld(){
        return world;
    }

    public final Set<Chunk> getLoaders(){
        return loaders;
    }

    public boolean damage(DamageSource source, float damage){

        if (source.isExplosion()) {
            damage *= DamageMultiplier.EXPLOSION.multiplier;
        }
        else if (source.isFire()) {
            damage *= DamageMultiplier.FIRE.multiplier;
        }
        else if (source instanceof GunSource) {
            damage *= DamageMultiplier.GUN.multiplier;
        }
        else if (source.isMagic()) {
            damage *= DamageMultiplier.MAGIC.multiplier;
        }
        else
             damage *= DamageMultiplier.DEFAULT.multiplier;

        setHealth(this.health - damage);
        if (health <= 0) {
            remove(false, true);
            return false;
        }
        return true;
    }

    public void playSound(ArtilleryPart part){
       world.playSound(part.getLocation(world),part.getSoundHurt(), SoundCategory.BLOCKS,1,1);
    }

     public double getHealth(){
        return health;
     }

    public final synchronized void setHealth(double health){
        this.health = health;
    }


    protected final void initLoadedChunks(){
        double totalDistanceBarrel = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (barrel.length * SMALL_BLOCK_LENGTH);
        double totalDistanceBase = 0;

        for (EntityArmorStand[] array: base)
            if (array.length > totalDistanceBase)
                totalDistanceBase = array.length;

            double totalDistance = Math.max(totalDistanceBarrel,(LARGE_BLOCK_LENGTH*totalDistanceBase));

            double circle = Math.PI * 2;
             Location loc = pivot.getLocation(world).clone();
            for (double rads=0;rads < circle;rads+= Math.PI/4) {

                double z = totalDistance * Math.cos(rads);
                double x = -totalDistance * Math.sin(rads);

                Chunk chunk = world.getChunkAt(loc.getBlockX()+(int)x, loc.getBlockZ()+(int)z);
                loaders.add(chunk);
            }
    }


}
