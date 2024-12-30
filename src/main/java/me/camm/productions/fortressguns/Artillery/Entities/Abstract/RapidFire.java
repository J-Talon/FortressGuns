package me.camm.productions.fortressguns.Artillery.Entities.Abstract;


import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Components.FireTrigger;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import me.camm.productions.fortressguns.Inventory.Abstract.InventoryGroup;
import me.camm.productions.fortressguns.Util.ArtilleryMaterial;
import me.camm.productions.fortressguns.Util.StandHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.Vector3f;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.util.EulerAngle;

import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/*
 * @author CAMM
 */
public abstract class RapidFire extends ArtilleryRideable {


    protected static final ItemStack BARREL_ITEM, MUZZLE_ITEM, SEAT_ITEM;
    protected static final Vector3f rightArm, leftArm, body, rightLeg, leftLeg;


    protected double barrelHeat;
    protected boolean isJammed;

    protected long lastInteractionTime;




    static {

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

    static {
        CASING = new ItemStack(Material.IRON_NUGGET);
        if (CASING.getItemMeta() != null) {
            ItemMeta meta = CASING.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Bullet Casing");
            CASING.setItemMeta(meta);
        }
    }




    public RapidFire(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
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
    protected boolean spawnParts(){

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
            remove(false, true);
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

    public double getBarrelHeat() {
        return barrelHeat;
    }

    public void setBarrelHeat(double barrelHeat) {
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

        final int BAR_LENGTH = 50;
        final int MAX_OCT = 255;
        final String BAR = "|";

        String progressBar = BAR.repeat(BAR_LENGTH);
        net.md_5.bungee.api.ChatColor ammoColor;

        if (isJammed()) {
            ammoColor = net.md_5.bungee.api.ChatColor.DARK_PURPLE;
        }
    else {
            ammoColor = net.md_5.bungee.api.ChatColor.DARK_AQUA;
        }




        pivot(Math.toRadians(human.getXRot()), Math.toRadians(human.getHeadRotation()));
        Player player = (Player)human.getBukkitEntity();

        float heatPercent = (float)barrelHeat / 100;   ///heat is from [0-100]
        int b = (int) ((1 - heatPercent) * MAX_OCT);    //blue
        int g = (int) ((1 - 0.8 * heatPercent) * MAX_OCT);   //green

        net.md_5.bungee.api.ChatColor tempColor;
        if (heatPercent > 0.75) {
            long modulus = System.currentTimeMillis() % 1000;
            if (modulus > 500) {
                tempColor = net.md_5.bungee.api.ChatColor.WHITE;
            }
            else {
                tempColor = net.md_5.bungee.api.ChatColor.of(new Color(MAX_OCT, g, b));
            }
        }
        else {
            tempColor = net.md_5.bungee.api.ChatColor.of(new Color(MAX_OCT, g, b));
        }


        int barTempAmount = (int)Math.ceil(heatPercent * BAR_LENGTH);
        TextComponent tempReference, componentLeft, componentRight;


        /////left side
        String displayTemp = ""+(int)Math.round(barrelHeat);
        displayTemp = "[H:"+ ("0".repeat(3-displayTemp.length())) + displayTemp +"%]";

        componentLeft = new TextComponent(displayTemp);
        componentLeft.setColor(net.md_5.bungee.api.ChatColor.GOLD);


        tempReference = new TextComponent(progressBar.substring(0, BAR_LENGTH - barTempAmount));
        tempReference.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        componentLeft.addExtra(tempReference);


        tempReference = new TextComponent(progressBar.substring(BAR_LENGTH - barTempAmount));
        tempReference.setColor(tempColor);
        componentLeft.addExtra(tempReference);



        /////
        ///right side
        if (!requiresReloading()) {
            componentRight =  new TextComponent(progressBar);
            componentRight.setColor(ammoColor);
            tempReference = new TextComponent("[∞IFNTE]");

        }
        else {

            int maxAmmo = getMaxAmmo();
            int currentAmmo = getAmmo();

            int ammoPercentLeft;
            if (maxAmmo == 0) {
                ammoPercentLeft = Math.min(BAR_LENGTH, currentAmmo);
            }
            else {
                ammoPercentLeft = (int) (((float)currentAmmo / maxAmmo) * BAR_LENGTH);
            }

            componentRight = new TextComponent(progressBar.substring(0, ammoPercentLeft));
            componentRight.setColor(ammoColor);

            tempReference = new TextComponent(progressBar.substring(ammoPercentLeft));
            tempReference.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
            componentRight.addExtra(tempReference);

            int left = (""+maxAmmo).length() - (""+currentAmmo).length();
            tempReference = new TextComponent("["+("0".repeat(left)) + Math.min(getAmmo(),999) + ":AML]");
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
}
