package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LightArtillery extends FieldArtillery
{

    private static final int SMALL_THRESH = 2;

    private static double maxHealth;
    private static long fireCooldown;


    public LightArtillery(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world,loader,aim);
        barrel = new ArtilleryPart[5];
        base = new ArtilleryPart[3][3];
    }

    static {
        maxHealth = 20;
        fireCooldown = 3000;
    }
    public static void setMaxHealth(double maxHealth) {
        LightArtillery.maxHealth = maxHealth;
    }

    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.STANDARD_HEAVY == item;
    }

    public static void setCooldown(long fireCooldown) {
        LightArtillery.fireCooldown = fireCooldown;
    }

    public synchronized boolean canFire(){
        return (getAmmo() > 0 || !requiresReloading()) && canFire && System.currentTimeMillis()-lastFireTime >= fireCooldown;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FIELD_LIGHT;
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @Override
    public double getVectorPower() {
        return 4;
    }

    @Override
    protected boolean spawnBaseParts() {
        double rads = -Math.PI/3;
        int bar = 0;
        double radsIfTrue = Math.PI;
        double radsIfFalse = 2 * Math.PI / 3;

        return super.spawnBaseWithDegrees(bar, rads, radsIfTrue, radsIfFalse, true);
    }


    @Override
    public List<ArtilleryPart> getParts(){
        List<ArtilleryPart> parts = new ArrayList<>(Arrays.asList(barrel));
        for (ArtilleryPart[] segment: base)
            parts.addAll(Arrays.asList(segment));
        parts.add(pivot);
        parts.add(rotatingSeat);
        return parts;

    }

    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }


}
