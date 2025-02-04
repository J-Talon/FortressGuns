package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;

import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.LightShell;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import me.camm.productions.fortressguns.Inventory.Abstract.ConstructInventory;
import me.camm.productions.fortressguns.Inventory.Abstract.InventoryCategory;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;

/*
 * @author CAMM
 */
public class LightFlak extends RapidFire {
    private  static long cooldown;
    private static int magSize;

    private static double jamPercent, overheat, maxHealth;
    private static long inactiveHeatTicks;
    private static double heatDissipation;






    public LightFlak(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }

    static {
        maxHealth = 15;
        cooldown = 1000;
        jamPercent = 0;
        overheat = 1;
        inactiveHeatTicks = 2000;
        heatDissipation = 1;
    }



    public static void setCooldown(long cooldown) {
        LightFlak.cooldown = cooldown;
    }

    public static void setMaxHealth(double maxHealth) {
        LightFlak.maxHealth = maxHealth;
    }

    public static void setMagSize(int magSize) {
        LightFlak.magSize = magSize;
    }

    public static void setJamPercent(double jamPercent) {
        LightFlak.jamPercent = jamPercent;
    }

    public static void setOverheat(double overheat) {
        LightFlak.overheat = overheat;
    }

    public static void setInactiveHeatTicks(long inactiveHeatTicks) {
        LightFlak.inactiveHeatTicks = inactiveHeatTicks;
    }

    public static void setHeatDissipation(double heatDissipation) {
        LightFlak.heatDissipation = heatDissipation;
    }

    public double getVectorPower() {
        return 4;
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FLAK_LIGHT;
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

        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();


        Player operator = null;
        if (passengers.size() == 0)
            return;

        Entity e = passengers.get(0);
        if (e instanceof Player)
            operator = (Player) e;

        if (operator == null)
            return;

        Location muzzle = barrel[barrel.length - 1].getEyeLocation().clone().add(0, 0.2, 0);
        boolean jammed = random.nextDouble() < jamPercent;

        if (jammed || isJammed()) {
            operator.sendMessage(ChatColor.RED+"Gun is jammed!");
            world.playSound(muzzle, Sound.ITEM_FLINTANDSTEEL_USE,SoundCategory.BLOCKS,1f,0f);
            setJammed(true);
            return;
        }

        barrelHeat = Math.min(100, overheat + barrelHeat);

        if (barrelHeat >= 100) {
            ArtilleryPart barrelPart = barrel[random.nextInt(barrel.length)];
            barrelPart.setFireTicks(barrelPart.getFireTicks() + 20);
            world.spawnParticle(Particle.LAVA,barrelPart.getEyeLocation(),5,0,0,0,1);
        }

        createFlash(muzzle);

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

        net.minecraft.world.level.World nmsWorld = ((CraftWorld) world).getHandle();


        EntityPlayer nmsOperator = ((CraftPlayer) operator).getHandle();
        LightShell shell = new FlakLightShell(nmsWorld, muzzle.getX(), muzzle.getY(), muzzle.getZ(), nmsOperator, this);

        shell.setMot(projectileVelocity.getX(), projectileVelocity.getY(), projectileVelocity.getZ());
        nmsWorld.addEntity(shell);

        ConstructInventory inv = interactionInv.getInventoryByCategory(InventoryCategory.RELOADING);
        if (inv != null)
            inv.updateState();


    }


    @Override
    public void rideTick(EntityHuman human) {
        super.rideTick(human);

        if (canFire) {

            long timeElapsed = System.currentTimeMillis() - lastFireTime;
            if (timeElapsed < inactiveHeatTicks)
                return;

            barrelHeat = Math.max(0, barrelHeat - heatDissipation);
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
        return (ammo > 0 || !requiresReloading()) && !isJammed;
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
