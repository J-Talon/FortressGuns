package me.camm.productions.fortressguns.Artillery.Entities.Abstract;


import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.LightShell;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ArtilleryMaterial;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.StandHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.Vector3f;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.util.EulerAngle;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.*;


/*
 * @author CAMM
 */
public abstract class RapidFire extends ArtilleryRideable {


    protected static final ItemStack BARREL_ITEM, MUZZLE_ITEM, SEAT_ITEM;
    protected static final Vector3f rightArm, leftArm, body, rightLeg, leftLeg;


    protected volatile double barrelHeat;
    protected boolean isJammed;

    protected long lastInteractionTime;

    protected static final String PROGRESS_BAR;
    protected static final int BAR_LENGTH;


    static {
        BAR_LENGTH = 50;
        PROGRESS_BAR = "|".repeat(BAR_LENGTH);
        BARREL_ITEM = ArtilleryMaterial.STANDARD_BODY.asItem();
        MUZZLE_ITEM = ArtilleryMaterial.SMALL_BARREL.asItem();
        SEAT_ITEM = ArtilleryMaterial.SEAT.asItem();


        rightArm = new Vector3f(50,0,0);
        leftArm = new Vector3f(50,0,0);  body = new Vector3f(330,0,0);
        rightLeg = new Vector3f(267,0,0);  leftLeg = new Vector3f(267,0,0);

    }

    protected Vector projectileVelocity;
    protected static ItemStack CASING;

    protected static final Random random = new Random();


    protected FireTrigger triggerHandle;

    protected static Map<Byte, net.md_5.bungee.api.ChatColor> heatColours;

    static {
        heatColours = new HashMap<>();
        CASING = new ItemStack(Material.IRON_NUGGET);
        if (CASING.getItemMeta() != null) {
            ItemMeta meta = CASING.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Bullet Casing");
            CASING.setItemMeta(meta);
        }
    }




    public RapidFire(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        barrel = new ArtilleryPart[4];
        base = new ArtilleryPart[1][1];
        barrelHeat = 0;
        isJammed = false;
        projectileVelocity = new Vector(0,0,0);
        lastInteractionTime = 0;
    }

    public List<ArtilleryPart> getParts() {
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        parts.add(pivot);
        parts.add(triggerHandle);
        parts.addAll(Arrays.asList(base[0]));
        parts.add(rotatingSeat);

        return parts;
    }

    @Override
    protected void initInventories() {
        interactionInv = new InventoryGroup.RapidGroup(this);
    }

    @Override
    protected boolean spawnTurretParts() {

        for (int slot = 0;slot < barrel.length;slot++) {
            double totalDistance = (0.5 * LARGE_BLOCK_LENGTH) + (slot * SMALL_BLOCK_LENGTH);

            double height = -totalDistance*Math.sin(aim.getX());
            double horizontalDistance = totalDistance*Math.cos(aim.getX());

            double z = horizontalDistance*Math.cos(aim.getY());
            double x = -horizontalDistance*Math.sin(aim.getY());

            Location centre = pivot.getLocation(world).clone();
            ArtilleryPart stand;

            //if it is small, add 0.75 so that it is high enough
            stand = StandHelper.createInvisiblePart(centre.add(x, height + 0.75, z), MUZZLE_ITEM, aim, world, this);
            if (stand == null)
                return false;

            stand.setSmall(true);

            barrel[slot] = stand;
        }
        return true;
    }

    @Override
    public void positionSeat() {

        // aim = new EulerAngle(vertAngle,horAngle,0);
        Location rotatingSeat = base[0][0].getLocation(world);
        EulerAngle aim = this.getAim();
        double horAngle = aim.getY();

        double rotSeatZ = -Math.cos(horAngle);
        double rotSeatX = Math.sin(horAngle);

        rotatingSeat.add(rotSeatX,0,rotSeatZ);
        this.rotatingSeat.teleport(rotatingSeat.getX(),rotatingSeat.getY(), rotatingSeat.getZ());
        this.rotatingSeat.setRotation(0,(float)horAngle);
        this.triggerHandle.teleport(rotatingSeat.add(0,1,0));

    }





    @Override
    protected boolean instantiateParts(){

        pivot = StandHelper.createCore(loc, BARREL_ITEM,aim,world, this);
        if (pivot == null)
            return false;

        if (!spawnBaseParts())
            return false;

        ArtilleryPart support = base[0][0];

        Location seatPos = getSeatLocation(support);
        seatPos.add(0,0.5,0);

        this.rotatingSeat = StandHelper.createInvisiblePart(seatPos, SEAT_ITEM,new EulerAngle(0,aim.getX(), 0),world,this);
        this.triggerHandle = StandHelper.createTrigger(seatPos.clone().add(0,1,0),world, this);

        if (rotatingSeat == null || triggerHandle == null)
            return false;


        if (!spawnTurretParts())
            return false;


        calculateLoadedChunks();
        if (health <= 0)
            setHealth(getMaxHealth());

        return true;
    }



    @Override
    protected boolean spawnBaseParts() {
        ArtilleryPart support;

        support = StandHelper.createVisiblePart(loc.clone().subtract(0,0.5,0),null,new EulerAngle(0,0,0), world,this);

        if (support == null)
            return false;


        support.setArms(true);
        support.setPose(rightArm, leftArm, body, rightLeg, leftLeg);
        base[0][0] = support;
        return true;
    }

    public Component getSeat(){
        return rotatingSeat;
    }




    @Override
    public synchronized void pivot(double vertAngle, double horAngle)
    {
        if (dead)
            return;

        if (isInvalid()) {
            destroy(false, true);
            dead = true;
            return;
        }

        positionSeat();


        //for all of the armorstands making up the barrel,
        for (int slot=0;slot< barrel.length;slot++)
        {
            ArtilleryPart stand = barrel[slot];

            double totalDistance;

            //getting the distance from the pivot

            totalDistance = (0.5*LARGE_BLOCK_LENGTH) + (slot * smallBlockDist);

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
            Location teleport = centre.add(x, height + 0.75, z);
            stand.teleport(teleport.getX(),teleport.getY(),teleport.getZ());

        }
    }

    public synchronized double getBarrelHeat() {
        return barrelHeat;
    }

    public synchronized void setBarrelHeat(double barrelHeat) {
        this.barrelHeat = barrelHeat;
    }

    public boolean isJammed() {
        return isJammed;
    }

    public void setJammed(boolean jammed) {
        isJammed = jammed;
    }

    public abstract long getInactiveHeatTicks();

    public abstract double getHeatDissipationRate();



    @Override
    public void rideTick(EntityHuman human) {


        final int MAX_OCT = 255;

        net.md_5.bungee.api.ChatColor ammoColor;

        long modulus = System.currentTimeMillis() % 1000;
        boolean changeColor = modulus > 500;

        if (isJammed() && changeColor) {
            ammoColor = net.md_5.bungee.api.ChatColor.DARK_PURPLE;
        }
    else {
            ammoColor = net.md_5.bungee.api.ChatColor.DARK_AQUA;
        }


        pivot(Math.toRadians(human.getXRot()), Math.toRadians(human.getHeadRotation()));
        Player player = (Player)human.getBukkitEntity();

        double heat = getBarrelHeat();
        float heatPercent = (float)heat / 100;   ///heat is from [0-100]
        byte displayHeat = (byte)Math.round(heat);

        net.md_5.bungee.api.ChatColor tempColor;
        if (heatPercent > 0.75 && changeColor) {
            tempColor = net.md_5.bungee.api.ChatColor.WHITE;
        }
        else {
            if (heatColours.containsKey(displayHeat)) {
                tempColor = heatColours.get(displayHeat);
            }
            else {
                tempColor = net.md_5.bungee.api.ChatColor.of(
                        new Color(MAX_OCT,
                                (int) ((1 - 0.8 * heatPercent) * MAX_OCT),  //blue
                                (int) ((1 - heatPercent) * MAX_OCT)));   //green
                heatColours.put(displayHeat, tempColor);
            }
        }


        int barTempAmount = (int)Math.ceil(heatPercent * BAR_LENGTH);
        TextComponent tempReference, componentLeft, componentRight;


        /////left side
        String displayTemp = ""+displayHeat;
        displayTemp = "[H:"+ ("0".repeat(3-displayTemp.length())) + displayTemp +"%]";

        componentLeft = new TextComponent(displayTemp);
        componentLeft.setColor(net.md_5.bungee.api.ChatColor.GOLD);


        tempReference = new TextComponent(PROGRESS_BAR.substring(0, BAR_LENGTH - barTempAmount));
        tempReference.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        componentLeft.addExtra(tempReference);


        tempReference = new TextComponent(PROGRESS_BAR.substring(BAR_LENGTH - barTempAmount));
        tempReference.setColor(tempColor);
        componentLeft.addExtra(tempReference);



        /////
        ///right side
        if (!requiresReloading()) {
            componentRight =  new TextComponent(PROGRESS_BAR);
            componentRight.setColor(ammoColor);
            tempReference = new TextComponent("[∞IFNTE]");

        }
        else {

            int maxAmmo = getMaxAmmo();
            int currentAmmo = getAmmo();

            int ammoPercentLeft;
            int left;

            int max = Math.max(3 - ("" + currentAmmo).length(), 0);
            if (maxAmmo < 0) {
                ammoPercentLeft = Math.min(BAR_LENGTH, currentAmmo);
                left = max;
            }
            else if (maxAmmo == 0){
               ammoPercentLeft = 0;
               left = 3;
            }
            else {
                ammoPercentLeft = (int) (((float)currentAmmo / maxAmmo) * BAR_LENGTH);
                left = max;
            }

            componentRight = new TextComponent(PROGRESS_BAR.substring(0, ammoPercentLeft));
            componentRight.setColor(ammoColor);

            tempReference = new TextComponent(PROGRESS_BAR.substring(ammoPercentLeft));
            tempReference.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
            componentRight.addExtra(tempReference);

            tempReference = new TextComponent("["+("0".repeat(left)) + Math.min(getAmmo(),999) + ":LDA]");
        }

        tempReference.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        componentRight.addExtra(tempReference);


        ////

        //middle
        double roundHealth = Math.round(health * 100d) / 100d;
        tempReference = new TextComponent("[H "+roundHealth +" P]");
        tempReference.setColor(net.md_5.bungee.api.ChatColor.RED);

        componentLeft.addExtra(tempReference);
        componentLeft.addExtra(componentRight);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, componentLeft);
    }

    @Override
    public void updateOnInteraction() {
        long next = Math.max(lastInteractionTime, lastFireTime);

        long diff = System.currentTimeMillis() - next - getInactiveHeatTicks();

        if (diff < 0)
            return;

        //diff is in millis
        //convert to ticks  --> (n * 20) / 1000 == 0.02*n
        diff = (long)(diff * 0.02);

        setBarrelHeat(Math.max(0,getBarrelHeat() - (diff * getHeatDissipationRate())));
        lastInteractionTime = System.currentTimeMillis();
    }


    protected @Nullable LightShell createProjectile(net.minecraft.world.level.World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
        AmmoItem item = getLoadedAmmoType();
        if (item == null) {
            return null;
        }
        return (LightShell)item.create(world, x,y,z, shooter, source);
    }
}
