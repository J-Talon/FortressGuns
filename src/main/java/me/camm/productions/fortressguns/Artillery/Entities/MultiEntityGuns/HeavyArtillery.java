package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FieldArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigManager;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.SectionFieldHeavy;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigurationHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

import java.util.List;


public class HeavyArtillery extends FieldArtillery implements ConfigurationHolder<SectionFieldHeavy>
{

    private static final int SMALL_THRESH = 1;

    private static double maxHealth;
    private static long cooldown;
    private static boolean configApplied;

    public HeavyArtillery(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        barrel = new ArtilleryPart[8];
        base = new ArtilleryPart[4][3];

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getFieldHeavy());
    }

    static {
        //default values
        configApplied = false;
        maxHealth = 80;
        cooldown = 5000;
    }


    public void setStaticDefaults(SectionFieldHeavy heavy) {
        configApplied = true;
        if (heavy == null) return;
        maxHealth = heavy.getHealth();
        cooldown = heavy.getCooldown();

    }


    @Override
    public double getVectorPower() {
        return 6;
    }

    ///
    @Override
    public synchronized boolean canFire(){
        return (getAmmo() > 0 || !requiresReloading()) && loadedAmmoType != null && canFire && System.currentTimeMillis() - lastFireTime >= cooldown;
    }

    @Override
    public List<Component> getParts(){
        List<Component> parts = super.getParts();
        parts.add(pivot);
        parts.add(rotatingSeat);
        return parts;
    }


    @Override
    public ConstructType getType() {
        return ConstructType.FIELD_HEAVY;
    }


    @Override
    public double getMaxHealth() {
        return maxHealth;
    }


    @Override
    protected void spawnBaseParts() {
        double rads = 0;
        int bar = 0;
        spawnBaseWithDegrees(bar, rads, -1, 2 * Math.PI / 4, false);
    }


    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }


}
