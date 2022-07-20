package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemCreator;
import me.camm.productions.fortressguns.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.ArtilleryInventory;
import me.camm.productions.fortressguns.Util.ExplosionEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/*
@author CAMM
Abstract class for the artillery pieces
Superclass for all complex entities which are artillery pieces
 */
public abstract class Artillery implements InventoryHolder, Construct {



    protected Plugin plugin;
    protected ArtilleryPart[] barrel;
    protected ArtilleryPart[][] base;
    protected ArtilleryCore pivot;
    protected EulerAngle aim;//
    protected final ChunkLoader handler;


    protected volatile double health;//--

    private final Set<Chunk> loaders;//

    protected Location loc; //
    protected World world;//

    protected boolean dead;//
    protected boolean loaded;

    protected final static double LARGE_BLOCK_LENGTH = 0.6;
    protected final static double SMALL_BLOCK_LENGTH = 0.4;

    protected volatile double currentLargeLength;//
    protected volatile double currentSmallLength;//
    protected volatile boolean canFire;

    protected ArtilleryInventory inventory;

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


    public Artillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {

        this.plugin = FortressGuns.getInstance();
        this.loc = loc;
        this.world = world;
        this.lastFireTime = System.currentTimeMillis();
        this.handler = loader;


        this.loaders = new HashSet<>();
        currentLargeLength = LARGE_BLOCK_LENGTH;
        currentSmallLength = SMALL_BLOCK_LENGTH;
        health = 0;
        dead = false;
        this.aim = aim;
        this.canFire = true;
    }

    public abstract List<ArtilleryPart> getParts();
    public ArtilleryInventory getArtyInventory(){
        return inventory;
    }


    public EulerAngle getAim(){
        return aim;
    }

    public Location getLoc(){
        return loc;
    }

    public synchronized void pivot(double vertAngle, double horAngle)
    {
        if (dead)
            return;

        if (inValid()) {
            remove(false, true);
            dead = true;
            return;
        }


        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot
            if (stand.isSmall())
                totalDistance = (currentLargeLength*0.75 + 0.5*currentSmallLength) + (slot * currentSmallLength);
            else
                totalDistance = (slot+1)*currentLargeLength;


            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);


            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);




            aim = new EulerAngle(vertAngle,horAngle,0);
            //setting the rotation of all of the barrel armorstands.
            stand.setRotation(aim);
            pivot.setRotation(aim);


            Location centre = pivot.getLocation(world).clone();

            //teleporting the armorstands to be in line with the pivot
            if (stand.isSmall()) {
                Location teleport = centre.add(x, height + 0.75, z);
                stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
            }
            else {
                Location teleport = centre.clone().add(x, height, z);
                stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
            }
        }
    }

    public double[] getBasePositions(double radian){
        double distance = LARGE_BLOCK_LENGTH;
        double z = distance*Math.cos(radian);
        double x = -distance*Math.sin(radian);
        return new double[]{x,z};
    }

    public boolean chunkLoaded(){
        return loaded;
    }

    public void setChunkLoaded(boolean loaded){
        this.loaded = loaded;
    }


    public abstract void fire(double power, int recoilTime,double barrelRecoverRate, @Nullable Player player);
    public abstract void fire(@Nullable Player shooter);




    //to spawn an artillery piece, an external method calls spawn(). In the actual artillery classes however, the artillery
    // is actually spawned when the init method is called, however the spawn method calls the init method, so it's fine.
    public void spawn() {
        dead = false;
        loaded = true;
        world.playSound(loc,Sound.BLOCK_ANVIL_DESTROY,0.5f,1);
        init();
    }

    //this method actually spawns the artillery
    protected abstract void init();



    public abstract ArtilleryType getType();
    public abstract boolean canFire();
    public abstract double getMaxHealth();

    public final boolean inValid(){
        return (pivot == null || (!pivot.isAlive()) || health <= 0 || dead);
    }

    public final synchronized void unload(boolean dropItem, boolean exploded) throws IllegalStateException {
        List<ArtilleryPart> components = getParts();
        components.forEach(Entity::die);

        Location loc = pivot.getLocation(world).clone();

        if (exploded) {
            ExplosionEffect.explodeArtillery(loc, world);
        }

        if (dropItem) {
            ArtilleryItemCreator.packageArtillery(this);
        }

    }

    public final synchronized void remove(boolean dropItem, boolean exploded) throws IllegalStateException
    {
        handler.remove(loaders, this);
        unload(dropItem, exploded);
    }


    public ArtilleryCore getPivot(){
        return pivot;
    }



    public final void createFlash(Location origin) {


       List<Player> players = world.getPlayers();
       BlockData lightData = Material.LIGHT.createBlockData();
       BlockData airData = Material.AIR.createBlockData();
       final Location flash = origin.clone();


           players.forEach(player -> player.sendBlockChange(flash, lightData));


           new BukkitRunnable() {
               public void run() {
                   players.forEach(player -> player.sendBlockChange(flash, airData));
               }
           }.runTaskLater(FortressGuns.getInstance(), 5);


    }

    public void createShotParticles(Location muzzle){
        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),30,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);
        world.playSound(muzzle, Sound.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,2,0.2f);

    }


    public World getWorld(){
        return world;
    }

    public final Set<Chunk> getLoaders(){
        return loaders;
    }

    public boolean damage(DamageSource source, float damage){

        if (source.isExplosion()) {
            damage = Float.MAX_VALUE;
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

                Chunk chunk = world.getChunkAt((loc.getBlockX()+(int)x) >> 4, (loc.getBlockZ()+(int)z) >> 4);
                loaders.add(chunk);
            }
    }


}
