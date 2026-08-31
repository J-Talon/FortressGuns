package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructUtils;
import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import me.camm.productions.fortressguns.FortressGuns;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;


public abstract class ArtilleryRideable extends Artillery implements Rideable {

    protected ArtilleryPart rotatingSeat = null;  //artillery may have multiple seats in the future
    protected Player operator = null;

    public ArtilleryRideable(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
    }


    @Override
    public @Nullable Player getRider() {
        return operator;
    }

    @Override
    public Component getSeat() {
        return rotatingSeat;
    }



    @Override
    public void onDismount(Player player) {
        if (this.operator != null && operator.getUniqueId().equals(player.getUniqueId())) {
            this.operator = null;
            setCameraLocked(false);
            setInterpolatedAim(getAim());
        }
    }

    @Override
    public boolean onMount(Player player) {
        if (operator != null) return false;

        setCameraLocked(true);
        setInterpolating(false);
        this.operator = player;

        ArtilleryRideable artillery = this;

        new BukkitRunnable() {
            public void run() {
                if (artillery.isInvalid() || !artillery.chunkLoaded()) {
                    cancel();
                    return;
                }

                Entity vehicle = player.getVehicle();
                if (vehicle == null) {
                    cancel();
                    return;
                }

                if (!vehicle.getUniqueId().equals(rotatingSeat.getUniqueID())) {
                    artillery.kickOperator();
                    cancel();
                    return;
                }

                artillery.rideTick(player);
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);


        return true;
    }

    @Override
    public void kickOperator() {
        if (operator == null) return;
        operator.leaveVehicle();
        this.onDismount(operator);
    }



    //called every tick when the player is riding
    public void rideTick(Player human) {

        Location eyeLoc = human.getEyeLocation();
        pivot(Math.toRadians(eyeLoc.getPitch()), Math.toRadians(eyeLoc.getYaw()));
        double x, y;
        x = Math.round(Math.toDegrees(aim.getX()) * 1000d) / 1000d;
        y = Math.round(Math.toDegrees(aim.getY()) * 1000d) / 1000d;
        double roundHealth = Math.round(health * 100d) / 100d;
        Player.Spigot spigot = human.spigot();

        if (canFire()) {
            spigot.sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.GREEN+"Rotation: ["+x +" | "+y+"] Health: "+roundHealth));
        }
        else {
            spigot.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Rotation: ["+x+" | "+y+"] Health: " + roundHealth));
        }
    }



    //angle is around the y axis. so it is an angle which is horizontal to the ground
    protected void posSeatAbsoluteHorizon(Component seat, double xOffset, double yOffset, double vibrationOffsetY, double angAroundY) {


        EulerAngle aim = this.getAim();
        Location next = getSeatLocation( xOffset, yOffset, angAroundY);

        final double MAX_VIB = 0.25;  //artistic choice. too much makes it look bad, too little and they don't feel it
        if (operator != null) {
            double amount = Math.abs(vibrationOffsetY) > MAX_VIB ? (vibrationOffsetY > 0 ? MAX_VIB : -MAX_VIB) : vibrationOffsetY;
            next.add(0, amount, 0);
        }

        EulerAngle seatFacing = new EulerAngle(0,aim.getY(),0);
        seat.setRotation(seatFacing);
        seat.teleport(next);
    }


    protected Location getSeatLocation(double xOffset, double yOffset, double angle) {
        double seatAngle = angle + getAim().getY(); //get 90* offset
        double seatDistance = getBaseLength()*0.25;  //0.25 is for distance. arbitrary

        Location center = getCoreEntity().getLocation();

        double seatZ = seatDistance*Math.cos(seatAngle);
        double seatX = -seatDistance*Math.sin(seatAngle);

        seatZ = seatZ + (seatZ * xOffset);
        seatX = seatX + (seatX * xOffset);

        return center.clone().add(seatX, yOffset, seatZ);
    }





     Location getSeatLocation(ArtilleryPart referencePos) {

        Location reference = referencePos.getLocation(getWorld());
        EulerAngle aim = getAim();
        double rotSeatZ = -Math.cos(aim.getY());
        double rotSeatX = Math.sin(aim.getY());
        return reference.clone().add(rotSeatX, 0, rotSeatZ);

    }




}
