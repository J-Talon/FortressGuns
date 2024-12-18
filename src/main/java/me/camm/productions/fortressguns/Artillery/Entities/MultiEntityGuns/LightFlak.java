package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;

import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.LightShell;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;
import net.minecraft.server.level.EntityPlayer;
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
import java.util.Random;

/*
 * @author CAMM
 */
public class LightFlak extends RapidFire {
    private  static long cooldown;
    private static int magSize;

    private static double jamPercent, overheat, maxHealth;

    private static final Random random = new Random();

    //can't use until I find a way to renew the projectiles




    public LightFlak(Location loc, World world, ChunkLoader loader, EulerAngle aim) {
        super(loc, world, loader, aim);
    }

    static {
        maxHealth = 15;
        cooldown = 1000;
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

    public double getVectorPower() {
        return 4;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return loadingInventory.getInventory();
    }

    @Override
    public ArtilleryType getType() {
        return ArtilleryType.FLAK_LIGHT;
    }


    public void fireBarrage(){

        if (!canFire())
            return;

        int delayTicks = 1;
        final int shots = 5;
        canFire = false;
            new BukkitRunnable() {

                int fired = 0;
                @Override
                public void run() {

                    canFire = false;
                    fireOneShot();

                    fired ++;
                    if (fired >= shots) {
                        canFire = true;
                        lastFireTime = System.currentTimeMillis();
                        cancel();
                    }

                }
            }.runTaskTimer(plugin, 0,delayTicks);

    }


    private void fireOneShot(){

        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();


        Player operator = null;
        if (passengers.size() == 0)
            return;

        Entity e = passengers.get(0);
        if (e instanceof Player)
            operator = (Player)e;

        if (operator == null)
            return;

        Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
        createFlash(muzzle);
        world.playSound(muzzle,Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,2,1);

        double y = Math.tan(-aim.getX());
        double z = Math.cos(aim.getY());
        double x = -Math.sin(aim.getY());

        projectileVelocity.setX(x);
        projectileVelocity.setY(y);
        projectileVelocity.setZ(z);
        projectileVelocity.normalize().multiply(getVectorPower());
        projectileVelocity.add(new Vector(random.nextDouble()*0.1 - 0.05,random.nextDouble()*0.1 - 0.05,random.nextDouble()*0.1 - 0.05));


        net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();


        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();
        LightShell shell = new  FlakLightShell(nmsWorld,muzzle.getX(),muzzle.getY(),muzzle.getZ(),nmsOperator, this);

        shell.setMot(projectileVelocity.getX(), projectileVelocity.getY(), projectileVelocity.getZ());
        nmsWorld.addEntity(shell);


    }




    @Override
    public boolean canFire() {
        return canFire && System.currentTimeMillis() - lastFireTime >= cooldown;
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void fire(@Nullable Player shooter) {
        fireBarrage();
    }

}
