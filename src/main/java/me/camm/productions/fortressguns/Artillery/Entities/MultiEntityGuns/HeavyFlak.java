package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.FlakArtillery;
import me.camm.productions.fortressguns.Artillery.Entities.Property.AutoTracking;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigManager;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.SectionHeavyFlak;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigurationHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;


public class HeavyFlak extends FlakArtillery implements AutoTracking, ConfigurationHolder<SectionHeavyFlak> {

    private static final int SMALL_THRESH = 3;
    private static double maxHealth;
    private static long fireCooldown;
    private static boolean configApplied;


    public HeavyFlak(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
        barrel = new ArtilleryPart[12];
        base = new ArtilleryPart[4][4];

        this.vertRotSpeed = 3;
        this.horRotSpeed = 2;

        if (configApplied) return;

        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getHeavyFlak());
    }


    @Override
    public void setStaticDefaults(SectionHeavyFlak heavy) {
        configApplied = true;
        if (heavy == null) return;
        maxHealth = heavy.getHealth();
        fireCooldown = heavy.getCooldown();
    }

    static {
        maxHealth = 100;
        fireCooldown = 5000;
        configApplied = false;
    }

    public static void setMaxHealth(double health) {
        maxHealth = health;
    }

    public static void setCooldown(long cooldown) {
        fireCooldown = cooldown;
    }


    @Override
    public boolean canFire(){
        return (getAmmo() > 0 || !requiresReloading()) && loadedAmmoType != null && canFire && System.currentTimeMillis() - lastFireTime >= fireCooldown;
    }


    public void startAiming() {

        if (isAiming())
            return;

        setAiming(true);

            new BukkitRunnable() {
                public void run() {

                    if (!isAiming()) {
                        cancel();
                        return;
                    }
                    aimStatic();
                }
            }.runTaskTimer(FortressGuns.getInstance(), 0, 1);
    }

    public void setAiming(boolean aiming) {
        this.aiming = aiming;
    }

    @Override
    public boolean isAiming() {
        return aiming;
    }

    @Override
    public ConstructType getType() {
        return ConstructType.FLAK_HEAVY;
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
    public double getVectorPower() {
        return 7;
    }

    @Override
    protected int getSmallDistThreshold() {
        return SMALL_THRESH;
    }


}
