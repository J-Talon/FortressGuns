package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Property.Rideable;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ComponentAS;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

public abstract class ArtilleryRideable extends Artillery implements Rideable {

    protected volatile boolean hasRider;
    protected ArtilleryPart rotatingSeat = null;




    public ArtilleryRideable(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        this.hasRider = false;
    }


    public void setHasRider(boolean hasRider){
        this.hasRider = hasRider;
    }

    public boolean hasRider(){
        return hasRider;
    }


    @Override
    public ComponentAS getSeat() {
        return rotatingSeat;
    }

    public void onDismount() {
        setHasRider(false);
        setCameraLocked(false);
        setInterpolatedAim(getAim());
    }

    public void onMount() {
        setCameraLocked(true);
        setHasRider(true);
        setInterpolating(false);
    }

    //angle is around the y axis. so it is an angle which is horizontal to the ground
    protected void posSeatAbsoluteHorizon(ComponentAS seat, double xOffset, double yOffset, double vibrationOffsetY, double angAroundY) {


        EulerAngle aim = this.getAim();
        Location next = getSeatLocation( xOffset, yOffset, angAroundY);

        final double MAX_VIB = 0.25;  //artistic choice. too much makes it look bad, too little and they don't feel it
        if (hasRider()) {
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
