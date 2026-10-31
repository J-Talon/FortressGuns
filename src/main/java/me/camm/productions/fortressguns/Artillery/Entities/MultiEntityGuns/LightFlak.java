package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery.SectionLightFlak;
import me.camm.productions.fortressguns.Util.configv2.ConfigurationHolder;
import me.camm.productions.fortressguns.Util.vfx.FG_VFX;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.interact.item.Inventory.Abstract.InventoryCategory;
import org.bukkit.*;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/*
 * @author CAMM
 */
public class LightFlak extends RapidFire implements ConfigurationHolder<SectionLightFlak> {
    private  static long cooldown;
    private static int magSize;

    private static double jamPercent, overheat, maxHealth;
    private static long inactiveHeatTicks;
    private static double heatDissipation;
    private static boolean infinite;

    private static boolean configApplied;





    public LightFlak(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);

        if (configApplied) return;

        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getLightFlak());
    }

    static {
        maxHealth = 15;
        cooldown = 1000;
        jamPercent = 0;
        overheat = 1;
        inactiveHeatTicks = 2000;
        heatDissipation = 1;
        infinite = false;
        configApplied = false;
    }


    @Override
    public void setStaticDefaults(@Nullable SectionLightFlak data) {
        configApplied = true;
        if (data == null) return;

        maxHealth = data.getHealth();
        cooldown = data.getCooldown();
        jamPercent = data.getJamChance();
        overheat = data.getOverheat();
        inactiveHeatTicks = data.getHeatTicks();
        heatDissipation = data.getDissipation();
        infinite = data.infinite();

    }

    @Override
    public boolean requiresReloading() {
        return !infinite;
    }

    public double getVectorPower() {
        return 4;
    }

    @Override
    public ConstructType getType() {
        return ConstructType.FLAK_LIGHT;
    }


    public void fireBarrage(){

        if (!canFire())
            return;

        int delayTicks = 1;
        final int shots = 8;
        canFire = false;
            new BukkitRunnable() {

                int fired = 0;
                @Override
                public void run() {

                    if (!canFireSingle()) {
                        canFire = true;
                        cancel();
                        return;
                    }

                    lastFireTime = System.currentTimeMillis();
                    fireOneShot();

                    if (requiresReloading())
                         setAmmo(Math.max(0,getAmmo()-1));

                    fired ++;
                    if (fired >= shots) {
                        canFire = true;
                        cancel();
                    }

                }
            }.runTaskTimer(plugin, 0,delayTicks);

    }



    private void fireOneShot() {

        Entity rider = rotatingSeat.getRider();


        Player operator = null;
        if (rider == null) return;

        if (rider instanceof Player)
            operator = (Player) rider;

        if (operator == null)
            return;

        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);
        boolean jammed = random.nextDouble() < jamPercent;

        if (jammed || isJammed()) {
            operator.sendMessage(ChatColor.RED+"Gun is jammed!");
            world.playSound(muzzle, Sound.ITEM_FLINTANDSTEEL_USE,SoundCategory.BLOCKS,1f,1f);
            setJammed(true);
            return;
        }

        setBarrelHeat(Math.min(100, overheat + getBarrelHeat()));

        if (getBarrelHeat() >= 100) {
            ArtilleryPart barrelPart = barrel[random.nextInt(barrel.length)];
            barrelPart.setFireTicks(barrelPart.getFireTicks() + 20);
            world.spawnParticle(Particle.LAVA,barrelPart.getEyeLocation(),5,0,0,0,1);
        }

        ProjectileFG projectile = loadedAmmoType.create(world, muzzle.getX(), muzzle.getY(), muzzle.getZ(), operator, this);


        FG_VFX.createFlash(muzzle, world);

        if (requiresReloading())
            setAmmo(Math.max(0, getAmmo() - 1));


        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2, 1);

        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        projectileVelocity.setX(x);
        projectileVelocity.setY(y);
        projectileVelocity.setZ(z);
        projectileVelocity.normalize();

        final double INACCURACY = 1;
        final double INACURACY_FACTOR = 0.3;
        double halfInaccuracy = INACCURACY / 2;

        Vector inaccuracy = new Vector(random.nextDouble() * INACCURACY - halfInaccuracy, random.nextDouble() * INACCURACY - halfInaccuracy, random.nextDouble() * INACCURACY - halfInaccuracy);
        if (projectileVelocity.dot(inaccuracy) < 0) {
            inaccuracy.multiply(-1);
        }

        inaccuracy.multiply(INACURACY_FACTOR);
        projectileVelocity.multiply(getVectorPower());
        projectileVelocity.add(inaccuracy);
        projectile.setVelocity(projectileVelocity.getX(), projectileVelocity.getY(), projectileVelocity.getZ());
        projectile.addToWorld(world);

        ConstructInventory inv = interactionInv.getInventoryByCategory(InventoryCategory.RELOADING);
        if (inv != null)
            inv.updateState();


    }


    @Override
    public void rideTick(Player human) {
        super.rideTick(human);

        if (canFire) {

            long timeElapsed = System.currentTimeMillis() - lastFireTime;
            if (timeElapsed < inactiveHeatTicks)
                return;

            setBarrelHeat(Math.max(0, barrelHeat - heatDissipation));
        }

    }

    @Override
    public int getMaxAmmo() {
        return magSize;
    }

    @Override
    public boolean acceptsAmmo(AmmoItem item) {
        return AmmoItem.FLAK_LIGHT == item;
    }

    @Override
    public boolean canFire() {
        return (System.currentTimeMillis() - lastFireTime >= cooldown) && canFireSingle();
    }

    public boolean canFireSingle() {
        return (ammo > 0 || !requiresReloading()) && loadedAmmoType != null && !isJammed;
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void fire(@Nullable Player shooter) {
        fireBarrage();
    }

    @Override
    public long getInactiveHeatTicks() {
        return inactiveHeatTicks;
    }

    @Override
    public double getHeatDissipationRate() {
        return heatDissipation;
    }

}
