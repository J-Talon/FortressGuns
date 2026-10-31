package me.camm.productions.fortressguns.Artillery.Entities.Abstract;


import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
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
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.camm.productions.fortressguns.Util.Math.MathFG.nextVerticalAngle;


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

    protected volatile double largeBlockDist;//
    protected volatile boolean lengthChanged;
    protected volatile double smallBlockDist;//

    protected volatile boolean canFire;

    protected int recoilTime = 1;
    protected double barrelRecoverRate = 0.03;

    protected InventoryGroup interactionInv;

    protected long lastFireTime;//

    protected final static double [] offsetX;
    protected final static double [] offsetZ;

    protected double vibrationOffsetY;
    protected static boolean requiresReloading;
    protected final static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> flags;

    static {

         requiresReloading = false;

        flags = new HashSet<>(Arrays.asList(
                PacketPlayOutPosition.EnumPlayerTeleportFlags.a,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.b,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.c,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.d)
        );

        final int LEN = 12;
        offsetX = new double[LEN];
        offsetZ = new double[LEN];

        int slot = 0;
        for (double rotation = 0; rotation < 2*Math.PI; rotation += (2*Math.PI / LEN)) {
            double z = Math.cos(rotation);
            double x = -Math.sin(rotation);
            offsetX[slot] = x;
            offsetZ[slot] = z;

            slot ++;
        }
    }




    public Artillery(Location loc, World world, EulerAngle aim) {
        this.initialLoc = loc;
        this.plugin = FortressGuns.getInstance();
        this.world = world;
        this.lastFireTime = System.currentTimeMillis();



        this.occupiedChunks = new ArrayList<>();
        largeBlockDist = LARGE_BLOCK_LENGTH;
        smallBlockDist = SMALL_BLOCK_LENGTH;
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

    public static void setRequiresReloading(boolean requiresReloading) {
        Artillery.requiresReloading = requiresReloading;
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

    public ArtilleryPart[][] getBase() {
        return base;
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
    public synchronized boolean pivot(double vertAngle, double horAngle) //v = h.xRot  h = h.gHeadRot
    {

        if (isInvalid()) {
            destroy(false, true);
            dead = true;
            return false;
        }


        double currX,currY;
        currX = aim.getX();
        currY = aim.getY();


        //90 degrees in the up and down directions
        //in radians

        if (vertAngle <= getMaxVertAngle()) {
            vertAngle = Math.max(getMaxVertAngle(), vertAngle);
        }

        if (vertAngle >= getMinVertAngle()) {
            vertAngle = Math.min(vertAngle, getMinVertAngle());
        }

        final double TWO_PI = 2 * Math.PI;
        if (horAngle > TWO_PI || horAngle < -TWO_PI) {
            horAngle = horAngle % TWO_PI;
        }


        if (currX == vertAngle && currY == horAngle && !lengthChanged)
            return false;

        lengthChanged = false;

        vertAngle = nextVerticalAngle(currX, vertAngle, vertRotSpeed);


        //don't add PI to give an extra 180 * to the rotation (see StandHelper.getASFace(EntityHuman) )
        //since  -horizontalDistance*Math.sin(horAngle); already takes care of it.
        horAngle = MathFG.nextHorizontalAngle(currY, horAngle, horRotSpeed);

        Vector[] locations = new Vector[barrel.length];

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

            Location centre = pivot.getLocation();

            Vector teleport;
            if (barrelComponent.isSmall())
                teleport = centre.add(x, height + 0.75, z).toVector();
            else
                teleport = centre.add(x, height, z).toVector();


            //is at resting state
            if (!(smallBlockDist < SMALL_BLOCK_LENGTH || largeBlockDist < LARGE_BLOCK_LENGTH)) {
                if (ConstructUtils.isInBlock(barrelComponent, teleport)) {
                    return false;
                }
            }
            locations[slot] = teleport;
        }


        if (this instanceof Rideable)
            ((Rideable)this).positionSeat();

        aim = new EulerAngle(vertAngle,horAngle,0);
        pivot.setRotation(aim);

        for (int slot=0;slot< barrel.length;slot++) {
            ArtilleryPart barrelComponent = barrel[slot];
            Vector teleport = locations[slot];
            barrelComponent.teleport(teleport.getX(), teleport.getY(), teleport.getZ());
        }
        return true;
    }




    public abstract void fire(@Nullable Player shooter);


    public double[] getBasePositions(double radian){
        double distance = LARGE_BLOCK_LENGTH;
        double z = distance*Math.cos(radian);
        double x = -distance*Math.sin(radian);
        return new double[]{x,z};
    }

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


    protected synchronized void incrementSmallDistance(double increment){
        this.smallBlockDist += increment;
        lengthChanged = true;
    }

    protected synchronized void setSmallDistance(double dist) {
        this.smallBlockDist = dist;
        lengthChanged = true;
    }





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

    public abstract boolean canFire();
    public abstract double getMaxHealth();

    public abstract boolean acceptsAmmo(AmmoItem item);


    protected void shockwaveParticles() {
        ArmorStand core = (ArmorStand) this.getCoreEntity();
        Location loc = core.getEyeLocation();
        Location initial = loc.clone().add(0,-LARGE_BLOCK_LENGTH,0);

        for (int len = 0; len < offsetX.length; len ++) {
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, initial,0,offsetX[len],0,offsetZ[len],0.3f);

        }
    }

    protected List<Player> getShakenPlayers() {
        List<IntTuple2> chunks = getOccupiedChunks();
        final List<Player> vibrateFor = new LinkedList<>();
        for (IntTuple2 tup: chunks) {

            //it's fine cause at this point it should already be loaded
            Chunk c = world.getChunkAt(tup.getA(), tup.getB());
            org.bukkit.entity.Entity[] entities = c.getEntities();

            for (org.bukkit.entity.Entity e: entities) {
                if (e instanceof Player && e.getVehicle() == null)
                    vibrateFor.add((Player)e);
            }
        }
        return vibrateFor;
    }

    protected void shakeAnimation(List<Player> vibrateFor, int ticks) {

        final double MULT = 5;
        double offset = Math.sin(Math.PI + (0.5d * ticks * Math.PI)) * Math.max(-0.12 * ticks + 1, 0) * MULT;
        if (offset != 0) {
            setVibrationOffsetY(offset);
            for (Player observer : vibrateFor) {

                if (observer.getVehicle() != null)
                    continue;

                double magnitude = observer.getLocation().distanceSquared(getCurrentLocation());
                double baseSquared = getBaseLength();
                baseSquared *= baseSquared;
                baseSquared *= 1.25;

                magnitude = (1d/(baseSquared*baseSquared) * magnitude);
                magnitude *= -magnitude;
                magnitude += 1;

                magnitude = Math.max(0, magnitude);
                if (magnitude == 0)
                    continue;

                offset *= magnitude;

                EntityPlayer nms = ((CraftPlayer)observer).getHandle();

                PacketPlayOutPosition packet = new PacketPlayOutPosition(0,0,0,
                        0,
                        nms.getXRot() + ( (float) offset),
                        flags, 1, true);

                nms.b.sendPacket(packet);
            }
        }
        else {
            setVibrationOffsetY(0);
        }
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


    /**
     * If it's dead, health <= 0, or pivot is invalid, returns true
     * @return whether the artillery is valid
     */
    @Override
    public final boolean isInvalid() {
        return (pivot == null || (!pivot.isValid()) || health <= 0 || dead);
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


    //=========Chunks ==================================================================


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
