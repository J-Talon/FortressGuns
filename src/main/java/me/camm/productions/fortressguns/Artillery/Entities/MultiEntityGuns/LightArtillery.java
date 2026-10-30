package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery.SectionFieldLight;
import me.camm.productions.fortressguns.Util.configv2.ConfigurationHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LightArtillery extends FieldArtillery implements ConfigurationHolder<SectionFieldLight>
{

    private static final int SMALL_THRESH = 2;

    private static double maxHealth;
    private static long fireCooldown;
    private static boolean infinite;
    private static boolean configApplied;

    public LightArtillery(Location loc, World world, EulerAngle aim) {
        super(loc, world,aim);
        barrel = new ArtilleryPart[5];
        base = new ArtilleryPart[3][3];

        if (configApplied) return;

        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getFieldLight());

    }

    static {
        maxHealth = 20;
        fireCooldown = 3000;
        infinite = false;
        configApplied = false;
    }


    @Override
    public void setStaticDefaults(@Nullable SectionFieldLight data) {
        configApplied = true;
        if (data == null) return;

        fireCooldown = data.getCooldown();
        maxHealth = data.getHealth();
        infinite = data.isInfinite();

    }


    @Override
    public boolean requiresReloading() {
        return !infinite;
    }


    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.STANDARD_HEAVY == item;
    }

    public synchronized boolean canFire(){
        return (getAmmo() > 0 || !requiresReloading()) && loadedAmmoType != null && canFire && System.currentTimeMillis()-lastFireTime >= fireCooldown;
    }

    @Override
    public ConstructType getType() {
        return ConstructType.FIELD_LIGHT;
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
    protected void spawnBaseParts() {
        double rads = -Math.PI/3;
        int bar = 0;
        double radsIfTrue = Math.PI;
        double radsIfFalse = 2 * Math.PI / 3;
        spawnBaseWithDegrees(bar, rads, radsIfTrue, radsIfFalse, true);
    }


    @Override
    public List<Component> getParts(){
        List<Component> parts = new ArrayList<>(Arrays.asList(barrel));
        for (Component[] segment: base)
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
