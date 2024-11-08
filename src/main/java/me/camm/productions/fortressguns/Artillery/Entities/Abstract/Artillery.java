package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Properties.BulkLoaded;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.ArtilleryItems.ArtilleryItemCreator;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.ArtilleryInventory;
import me.camm.productions.fortressguns.Inventory.BulkLoadingInventory;
import me.camm.productions.fortressguns.Inventory.StandardLoadingInventory;
import me.camm.productions.fortressguns.Util.ExplosionEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/*
@author CAMM
Abstract class for the artillery pieces
Superclass for all complex entities which are artillery pieces
 */
public abstract class Artillery extends Construct implements InventoryHolder {


    protected int baseLength;

    protected volatile boolean hasRider;
    protected volatile int bullets;
    protected double vertRotSpeed = 1;
    protected double horRotSpeed = 1;
    protected ArtilleryPart rotatingSeat = null;
    protected Plugin plugin;
    protected ArtilleryPart[] barrel;
    protected ArtilleryPart[][] base;
    protected ArtilleryCore pivot;
    protected EulerAngle aim;//
    protected final ChunkLoader handler;


    protected volatile double health;//--
    private final Set<Chunk> occupiedChunks;//

    protected Location loc; //
    protected World world;//

    protected boolean dead;//
    protected boolean loaded;

    protected volatile double largeBlockDist;//
    protected volatile double smallBlockDist;//

    protected volatile boolean canFire;

    //default values for testing
    //===================
    protected double vectorPower = 6;
    protected int recoilTime = 1;
    protected double barrelRecoverRate = 0.03;
    //==============

    protected ArtilleryInventory loadingInventory;

    protected long lastFireTime;//

    //Enum for damage multipliers
    private enum DamageMultiplier{
        EXPLOSION(3),
        FIRE(1.5),
        GUN(1.2),
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
        this.hasRider = false;

        this.occupiedChunks = new HashSet<>();
        largeBlockDist = LARGE_BLOCK_LENGTH;
        smallBlockDist = SMALL_BLOCK_LENGTH;
        health = 0;
        dead = false;
        this.aim = aim;
        this.canFire = true;
        this.bullets = 0;

        if (this instanceof BulkLoaded) {
            loadingInventory = new BulkLoadingInventory(this);
        }
        else loadingInventory = new StandardLoadingInventory(this);

    }

    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        for (ArtilleryPart[] segment: base)
            parts.addAll(Arrays.asList(segment));

        return parts;
    }
    public @NotNull Inventory getInventory(){
        return loadingInventory.getInventory();
    }


    public ArtilleryInventory getLoadingInventory(){
        return loadingInventory;
    }


    public synchronized void setBullets(int bullets) {
    this.bullets = bullets;
    }

    public EulerAngle getAim(){
        return aim;
    }

    public Location getLoc(){
        return loc;
    }

    public ArtilleryPart[][] getBase() {
        return base;
    }

    public int getBaseLength() {
        if (baseLength <= 0 )
            baseLength = base[0].length;
        return baseLength;
    }

    public synchronized void setHasRider(boolean hasRider){
        this.hasRider = hasRider;
    }

    public synchronized boolean getHasRider(){
        return hasRider;
    }

    protected abstract void positionSeat();



    /*
    @param vertAngle, horAngle
    vertAngle: The vertical angle of the aim (in rads)
    horAngle: The horizontal angle of the aim (in rads)

     */
    public synchronized void pivot(double vertAngle, double horAngle) //v = h.xRot  h = h.gHeadRot
    {
        if (dead)
            return;

        if (isInvalid()) {
            remove(false, true);
            dead = true;
            return;
        }

        vertAngle = nextVerticalAngle(aim.getX(), vertAngle, vertRotSpeed);


        //don't add PI to give an extra 180 * to the rotation (see Construct.getASFace(EntityHuman) )
        //since  -horizontalDistance*Math.sin(horAngle); already takes care of it.
        horAngle = nextHorizontalAngle(aim.getY(), horAngle, horRotSpeed);

        positionSeat();


        aim = new EulerAngle(vertAngle,horAngle,0);
        pivot.setRotation(aim);

        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart barrelComponent = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot
            if (barrelComponent.isSmall())
                totalDistance = (largeBlockDist *0.75 + 0.5* smallBlockDist) + (slot * smallBlockDist);
            else
                totalDistance = (slot+1)* largeBlockDist;


            //height of the aim
            double height = -totalDistance*Math.sin(vertAngle);

            //hor dist of the aim component
            double horizontalDistance = totalDistance*Math.cos(vertAngle);



            //x and z distances relative to the pivot from total hor distance
            double z = horizontalDistance*Math.cos(horAngle);
            double x = -horizontalDistance*Math.sin(horAngle);
            //the - is to account for the 180* between players and armorstands

            //setting the rotation of all of the barrel armorstands.
            barrelComponent.setRotation(aim);

            Location centre = pivot.getLocation(world).clone();

            //teleporting the armorstands to be in line with the pivot
            if (barrelComponent.isSmall()) {
                Location teleport = centre.add(x, height + 0.75, z);
                barrelComponent.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
            }
            else {
                Location teleport = centre.clone().add(x, height, z);
                barrelComponent.teleport(teleport.getX(),teleport.getY(),teleport.getZ());
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

    public abstract void fire(@Nullable Player shooter);




    //to spawn an artillery piece, an external method calls spawn(). In the actual artillery classes however, the artillery
    // is actually spawned when the spawnParts method is called.
    public final boolean spawn() {
        dead = false;
        loaded = true;

        boolean spawned = spawnParts();
        if (spawned)
            loadPieces();

        return spawned;
    }

    public abstract ArtilleryType getType();
    public abstract boolean canFire();
    public abstract double getMaxHealth();

    protected void vibrateParticles() {
        ArtilleryCore core = this.getPivot();
        Location loc = core.getEyeLocation();

        for (double rotation = 0; rotation < 2*Math.PI; rotation += (2*Math.PI / 10)) {
            double z = Math.cos(rotation);
            double x = -Math.sin(rotation);

            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc.clone().add(0,-LARGE_BLOCK_LENGTH,0),0,x,0,z,0.1f);

        }

    }

    protected void loadPieces() {
        List<ArtilleryPart> parts = getParts();

        net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();
        for (ArtilleryPart part: parts) {
            nmsWorld.addEntity(part, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    public final boolean isInvalid(){
        return (pivot == null || (!pivot.isAlive()) || health <= 0 || dead);
    }

    public final synchronized void unload(boolean dropItem, boolean exploded) throws IllegalStateException {


        List<ArtilleryPart> components = getParts();
        try {
            components.forEach(Entity::die);
        }
        catch (NullPointerException ignored) {

        }

        Location loc;
        if (pivot != null)
            loc = pivot.getLocation(world).clone();
        else return;

        if (exploded) {
            ExplosionEffect.explodeArtillery(loc, world);
        }

        if (dropItem) {
            ArtilleryItemCreator.packageArtillery(this);
        }

    }

    public final synchronized void remove(boolean dropItem, boolean exploded) throws IllegalStateException
    {
        handler.remove(occupiedChunks, this);
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

        world.spawnParticle(Particle.FLASH,origin.getX(),origin.getY(), origin.getZ(),1,0,0,0,0.2);


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

    public final Set<Chunk> getOccupiedChunks(){
        return occupiedChunks;
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

    public ArtilleryPart getRotatingSeat() {
        return rotatingSeat;
    }


    /*
This method spawns the artillery components into the world
 */
    protected abstract boolean spawnParts();
    protected abstract boolean spawnBaseParts();
    protected abstract boolean spawnTurretParts();


    protected final void calculateLoadedChunks(){
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
            occupiedChunks.add(chunk);
        }
    }


}
