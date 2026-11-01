package me.camm.productions.fortressguns.Artillery.Entities.Abstract;


import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.IntTuple2;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import me.camm.productions.fortressguns.Util.Serialization.FactorySerialization;
import me.camm.productions.fortressguns.Util.Serialization.NBTSerializable;
import me.camm.productions.fortressguns.Util.vfx.FG_VFX;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.interact.item.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
@author CAMM
Abstract class for the artillery pieces
Superclass for all complex entities which are artillery pieces
 */
public abstract class Artillery extends Construct implements NBTSerializable<Integer[]> {


    protected int baseLength;

    protected volatile int ammo;
    protected AmmoItem loadedAmmoType;

    protected double vertRotSpeed = 1;
    protected double horRotSpeed = 1;
    protected Plugin plugin;
    protected ArtilleryPart[] barrel;
    protected ArtilleryPart[][] base;
    protected Location initialLoc;

    protected Component pivot;
    protected EulerAngle aim;//
    protected volatile EulerAngle interpolatedAim;
    protected volatile boolean cameraLocked;
    protected volatile boolean interpolating;

    protected volatile double health;//--
    private final List<IntTuple2> occupiedChunks;//
    protected World world;//

    protected volatile boolean lengthChanged;
    protected volatile boolean canFire;
    protected InventoryGroup interactionInv;

    protected long lastFireTime;//
    protected double vibrationOffsetY;





    public Artillery(Location loc, World world, EulerAngle aim) {
        this.initialLoc = loc;
        this.plugin = FortressGuns.getInstance();
        this.world = world;
        this.lastFireTime = System.currentTimeMillis();

        this.occupiedChunks = new ArrayList<>();

        health = 0;
        dead = false;
        cameraLocked = false;
        interpolating = false;
        this.aim = aim;
        this.interpolatedAim = aim;
        this.canFire = true;
        this.ammo = 0;
        this.vibrationOffsetY = 0;
        lengthChanged = false;
        loadedAmmoType = null;

        initInventories();
    }

    @Override
    public Integer[] serializeData() {
        Integer[] rotation = FactorySerialization.serializeRotation(getAim());
        int ammoType = FactorySerialization.serializeAmmo(getLoadedAmmoType());
        int type = FactorySerialization.serializeType(this);
        int health = FactorySerialization.serializeHealth(getHealth());
        return new Integer[]{type, rotation[0], rotation[1], rotation[2], ammoType, getAmmo(), health};
    }

    @Override
    public void unload() {

        if (!chunkLoaded())
            return;

        List<Component> parts = getParts();
        parts.remove(pivot);

        parts.forEach(Component::remove);

        Integer[] data = serializeData();
        int[] translation = new int[data.length];
        for (int index = 0; index < data.length; index ++) {
            translation[index] = data[index];
        }

        PersistentDataContainer container = pivot.getPDC();
        container.set(new NamespacedKey(plugin, FactorySerialization.getKey()), PersistentDataType.INTEGER_ARRAY,translation);
        setChunkLoaded(false);
    }



    protected synchronized void setCanFire(boolean canFire) {
        this.canFire = canFire;
    }

    protected synchronized boolean getCanFire() {
        return canFire;
    }

    protected abstract void initInventories();


    public abstract boolean requiresReloading();


    public int getAmmo() {
        return ammo;
    }

    public synchronized void setInterpolatedAim(EulerAngle angle) {
        double x = angle.getX();
        x = Math.max(getMaxVertAngle(), x);
        x = Math.min(getMinVertAngle(), x);

        final double TWO_PI = 2*Math.PI;
        final double NINETY_DEG = Math.PI / 2;

        double y = angle.getY();

        if (x > NINETY_DEG || x < -NINETY_DEG) {
            x = x % NINETY_DEG;
        }


        if (y > TWO_PI || y < -TWO_PI) {
            y = y % TWO_PI;
        }


        this.interpolatedAim = new EulerAngle(x, y, 0);
    }

    public synchronized EulerAngle getInterpolatedAim() {
        return interpolatedAim;
    }

    public synchronized boolean isCameraLocked() {
        return cameraLocked;
    }

    public synchronized void setCameraLocked(boolean cameraLocked) {
        this.cameraLocked = cameraLocked;
    }


    public abstract int getMaxAmmo();


    public List<Component> getParts() {
        List<Component> parts = new ArrayList<>(Arrays.asList(barrel));
        for (Component[] segment: base)
            parts.addAll(Arrays.asList(segment));

        return parts;
    }

    protected double getMaxVertAngle() {
        return -1.57;
    }

    protected double getMinVertAngle() {
        return 1.57;
    }

    public synchronized void setAmmo(int ammo) {
    this.ammo = ammo;
    }

    public AmmoItem getLoadedAmmoType() {
        return loadedAmmoType;
    }

    public void setLoadedAmmoType(AmmoItem loadedAmmoType) {
        this.loadedAmmoType = loadedAmmoType;

        ConstructInventory inv = this.getInventoryGroup().getInventoryByCategory(InventoryCategory.RELOADING);
        if (inv == null)
            return;

        inv.updateState();
    }

    public EulerAngle getAim(){
        return aim;
    }

    public Location getCurrentLocation(){
        return pivot.getLocation();
    }

    public InventoryGroup getInventoryGroup() {
        return interactionInv;
    }



    public abstract double getVectorPower();


    public int getBaseLength() {
        if (baseLength <= 0 )
            baseLength = base[0].length;
        return baseLength;
    }


    public synchronized void setInterpolating(boolean interpolating) {
        this.interpolating = interpolating;
    }

    public synchronized boolean isInterpolating() {
        return this.interpolating;
    }

    public void startPivotInterpolation() {

        if (interpolating) {
            return;
        }

        setInterpolating(true);

        ConstructInventory inv = getInventoryGroup().getInventoryByCategory(InventoryCategory.MENU);
        if (inv != null)
            inv.updateState();

        new BukkitRunnable() {
            @Override
            public void run() {

                if (isCameraLocked()) {
                    cancel();
                    return;
                }

                if (isInvalid() || !chunkLoaded()) {
                    cancel();
                    return;
                }

                double x,y;
                x = interpolatedAim.getX();
                y = interpolatedAim.getY();

                //
                boolean closeEnough = MathFG.aimIsCloseEnough(aim, x, y);

                if (closeEnough && !lengthChanged) {
                    setInterpolating(false);
                    cancel();
                    return;
                }

                if (!pivot(x,y)) {
                    world.playSound(pivot.getLocation(),Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,0.5f,0);
                    interpolatedAim = aim;
                    setInterpolating(false);
                    cancel();
                }

            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (inv != null)
                    inv.updateState();
                super.cancel();
            }
        }.runTaskTimer(FortressGuns.getInstance(), 0, 1);

    }



    /*
    @param vertAngle, horAngle
    vertAngle: The vertical angle of the aim (in rads)
    horAngle: The horizontal angle of the aim (in rads)

     */
    public abstract boolean pivot(double vertAngle, double horAngle); //v = h.xRot  h = h.gHeadRot



    public abstract void fire(@Nullable Player shooter);





    @Override
    public boolean chunkLoaded(){
        return loaded;
    }

    @Override
    public synchronized void setChunkLoaded(boolean loaded){
        this.loaded = loaded;
    }


    public synchronized double getVibrationOffsetY(){
        return vibrationOffsetY;
    }

    public synchronized void setVibrationOffsetY(double vib) {
        this.vibrationOffsetY = vib;
    }



    /**
     * If it's dead, health <= 0, or pivot is invalid, returns true
     * @return whether the artillery is valid
     */
    @Override
    public final boolean isInvalid() {
        return (pivot == null || (!pivot.isValid()) || health <= 0 || dead);
    }



    public org.bukkit.entity.Entity getCoreEntity(){
        return pivot.getEntity();
    }

    @Override
    public World getWorld(){
        return world;
    }

    @Override
    public double getHealth(){
        return health;
    }

    public abstract boolean canFire();

    public abstract double getMaxHealth();

    public abstract boolean acceptsAmmo(AmmoItem item);

    public ArtilleryPart[][] getBase() {
        return base;
    }


    public double[] getBasePositions(double radian){
        double distance = LARGE_BLOCK_LENGTH;
        double z = distance*Math.cos(radian);
        double x = -distance*Math.sin(radian);
        return new double[]{x,z};
    }

    @Override
    public void setHealth(double health){
        this.health = health;
    }

    @Override
    public boolean damage(double damage) {
        setHealth(getHealth() - damage);
        world.playSound(getCurrentLocation(),Sound.BLOCK_BELL_USE,SoundCategory.BLOCKS,1,1);

        if (health <= 0) {
            destroy(false, true);
            return false;
        }
        return true;
    }

    /*
These methods create the artillery components but don't spawn them.
see: loadPieces()
 */
    protected abstract void instantiateParts();
    protected abstract void spawnBaseParts();
    protected abstract void spawnTurretParts();



    //to spawn an artillery piece, an external method calls spawn(). In the actual artillery classes however, the artillery
    // is actually created when the spawnParts method is called, and loaded into the world (drawn) when loadPieces() is called
    public final boolean spawn(boolean ignoreSpaceRequirement) {
        dead = false;

        instantiateParts();

        if (!ignoreSpaceRequirement && !enoughSpace()) {
            return false;
        }

        setChunkLoaded(true);
        spawnPieces();

        return true;
    }




    protected void spawnPieces() {
        List<Component> parts = getParts();
        Logger logger = FortressGuns.getInstance().getLogger();

        for (Component part: parts) {
            boolean result = part.spawn();
            if (!result)
                logger.log(Level.WARNING, "Unable to properly spawn part for artillery");
        }
    }



    @Override
    public final synchronized void destroy(boolean dropItem, boolean exploded) throws IllegalStateException {

        getInventoryGroup().close();

        dead = true;
        List<Component> components = getParts();
        try {
            components.forEach(Component::remove);
        }
        catch (NullPointerException ignored) {

        }

        Location loc;
        if (pivot != null)
            loc = pivot.getLocation();
        else return;

        if (exploded) {
            FG_VFX.constructDestroy(loc, world);
        }

        if (dropItem) {
            ItemUtils.packageArtillery(this);
        }

        super.destroy(dropItem, exploded);
    }
    //========= Chunks ==================================================================


    @Override
    public final List<IntTuple2> getOccupiedChunks(){
        return occupiedChunks;
    }

    //this should only be necessary when updating where we are on file write/read.
    //basically this is only required when unloading / loading
    @Override
    public void calculateOccupiedChunks(){
        occupiedChunks.clear();
        double totalDistanceBarrel = (LARGE_BLOCK_LENGTH * 0.75 + 0.5 * SMALL_BLOCK_LENGTH) + (barrel.length * SMALL_BLOCK_LENGTH);
        double totalDistanceBase = getBaseLength();


        // + 1 for padding, ensures we're not off by like 0.1
        double totalDistance = Math.max(totalDistanceBarrel,(LARGE_BLOCK_LENGTH*totalDistanceBase)) + 1;


        double circle = Math.PI * 2;
        Location loc = initialLoc;

        IntTuple2 firstChunk = null;
        IntTuple2 lastChunk = null;
        for (double rads=0;rads < circle;rads+= Math.PI/4) {

            double z = totalDistance * Math.cos(rads);
            double x = -totalDistance * Math.sin(rads);

            // >> 4 is essentially dividing by 16
            //Chunk chunk = world.getChunkAt((loc.getBlockX()+(int)x) >> 4, (loc.getBlockZ()+(int)z) >> 4);
            IntTuple2 chunk = new IntTuple2((loc.getBlockX()+(int)x) >> 4, (loc.getBlockZ()+(int)z) >> 4);

            if (chunk.equals(firstChunk)) {
                lastChunk = chunk;
                continue;
            }

            if (chunk.equals(lastChunk)) {
                lastChunk = chunk;
                continue;
            }

            if (firstChunk == null) {
                firstChunk = chunk;
            }

            occupiedChunks.add(chunk);
            lastChunk = chunk;
        }
    }


    @Override
    public Chunk getCurrentChunk() {
        Location loc = getCurrentLocation();
        return world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    @Override
    public Location getInitialLoc() {
        return this.initialLoc;
    }

    @Override
    public Chunk getInitialChunk() {
        Location loc = initialLoc;
        return world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }


    protected @Nullable ProjectileFG createProjectile(World world, double x, double y, double z, Player shooter, Artillery source) {
        AmmoItem item = getLoadedAmmoType();
        if (item == null) {
            return null;
        }
        return item.create(world, x,y,z, shooter, source);
    }

}
