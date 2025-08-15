package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

import java.util.List;


public class HeavyArtillery extends FieldArtillery
{

    private static final int SMALL_THRESH = 1;
    private static double maxHealth;
    private static long cooldown;


    public HeavyArtillery(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        barrel = new ArtilleryPart[8];
        base = new ArtilleryPart[4][3];
    }

    static {
        //default values
        maxHealth = 80;
        cooldown = 5000;
    }


    public static void setMaxHealth(double health) {
        maxHealth = health;
    }

    public static void setFireCooldown(long cooldown) {
        HeavyArtillery.cooldown = cooldown;
    }

    @Override
    public double getVectorPower() {
        return 6;
    }

    ///
    @Override
    public synchronized boolean canFire(){
        return (getAmmo() > 0 || !requiresReloading()) && canFire && System.currentTimeMillis() - lastFireTime >= cooldown;
    }

    @Override
    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = super.getParts();
        parts.add(pivot);
        parts.add(rotatingSeat);
        return parts;
    }


    @Override
    public ConstructType getType() {
        return ConstructType.FIELD_HEAVY;
    }


    @Override
    protected synchronized void incrementSmallDistance(double increment) {
        super.incrementSmallDistance(increment);
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }


    @Override
    protected boolean spawnBaseParts() {
        double rads = 0;
        int bar = 0;
        return super.spawnBaseWithDegrees(bar, rads, -1, 2 * Math.PI / 4, false);
    }


    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }
}
