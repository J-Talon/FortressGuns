package me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.RapidFire;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;

import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.LightShell;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import me.camm.productions.fortressguns.Handlers.ChunkLoader;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;

import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


import java.util.List;

/*
Class that models a heavy machine gun which players can shoot and operate
 * @author CAMM
 *
 */
public class HeavyMachineGun extends RapidFire {

    private static int magSize;
    private static double overheat, jamPercent, maxHealth;

    private static long inactiveHeatTicks;
    private static double heatDissipation;


    static {

        maxHealth = 15;
        magSize = 100;
        jamPercent = 0;
        overheat = 0;
        inactiveHeatTicks = 2000;
        heatDissipation = 1;

    }

    public HeavyMachineGun(Location loc, World world, EulerAngle aim) {
        super(loc, world, aim);
    }
    public static void setMaxHealth(double max) {
        maxHealth = max;
    }

    public static void setMagSize(int magSize) {
        HeavyMachineGun.magSize = magSize;
    }

    public static void setOverheat(double overheat) {
        HeavyMachineGun.overheat = overheat;
    }

    public static void setJamPercent(double jamPercent) {
        HeavyMachineGun.jamPercent = jamPercent;
    }

    public static void setInactiveHeatTicks(long inactiveHeatTicks) {
        HeavyMachineGun.inactiveHeatTicks = inactiveHeatTicks;
    }

    public static void setHeatDissipation(double heatDissipation) {
        HeavyMachineGun.heatDissipation = heatDissipation;
    }

    @Override
    public double getVectorPower() {
        return 4;
    }

    @Override
    public int getMaxAmmo() {
        return magSize;
    }

    @Override
    public boolean acceptsAmmo(AmmoItem item) {
       return AmmoItem.STANDARD_LIGHT == item;
    }

    private void fireBurst(){

        int iterations = 0;
        long delayTicks = 2;
        final int shots = 2;  //this is intentional
                               // interact time is ~5 ticks, so 4 ticks waiting (2*2) + 1
                               // tick to receive next interaction --> the action is fluid under normal conditions



        if (!canFire())
            return;

        setCanFire(false);
        while (iterations < shots) {

            if (!canFireSingle())
                break;

            iterations ++;
            final int reference = iterations;
            new BukkitRunnable() {
                public void run() {

                    //this is because it starts later so while it may be true in the while,
                    //it might not be here
                    if (!canFireSingle()) {
                        setCanFire(true);
                        cancel();
                        return;
                    }

                    Location muzzle = barrel[barrel.length-1].getEyeLocation().clone().add(0,0.2,0);
                    fireSingleShot(muzzle);
                    if (reference >= shots)
                        setCanFire(true);

                    cancel();
                }
            }.runTaskLater(plugin, iterations * delayTicks);
        }
    }


    @Override
    public void rideTick(EntityHuman human) {
        super.rideTick(human);

        long timeElapsed = System.currentTimeMillis() - lastFireTime;
        if (timeElapsed < inactiveHeatTicks)
            return;

        setBarrelHeat(Math.max(0, getBarrelHeat() - heatDissipation));


    }

    private void fireSingleShot(Location muzzle) {

        List<Entity> passengers = rotatingSeat.getBukkitEntity().getPassengers();
        if (passengers.isEmpty()) {
            setCanFire(true);
            return;
        }

        Entity possibleOperator = passengers.get(0);
        Player operator = possibleOperator instanceof Player ? (Player) possibleOperator : null;
        if (operator == null) {
            setCanFire(true);
            return;
        }

        boolean jammed = random.nextDouble() < jamPercent;
        if (jammed || isJammed()) {
            world.playSound(muzzle, Sound.ITEM_FLINTANDSTEEL_USE,SoundCategory.BLOCKS,1f,1f);
            operator.sendMessage(ChatColor.RED+"Gun is jammed!");
            setCanFire(true);
            setJammed(true);
            return;
        }


        EntityPlayer nmsOperator = ((CraftPlayer)operator).getHandle();
        net.minecraft.world.level.World nms = ((CraftWorld)world).getHandle();
        LightShell shell = createProjectile(nms, muzzle.getX(),muzzle.getY(), muzzle.getZ(),nmsOperator,this);
        if (shell == null)
            return;

        setBarrelHeat(Math.min(100, overheat + getBarrelHeat()));

        if (getBarrelHeat() >= 100) {
            ArtilleryPart barrelPart = barrel[random.nextInt(barrel.length)];
            barrelPart.setFireTicks(barrelPart.getFireTicks() + 20);
            world.spawnParticle(Particle.LAVA,barrelPart.getEyeLocation(),5,0,0,0,1);
        }

        if (requiresReloading())
            setAmmo(Math.max(0, getAmmo() - 1));

        createFlash(muzzle);
        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,SoundCategory.BLOCKS,1f,2f);


        projectileVelocity = eulerToVec(aim).normalize();
        Vector direction = projectileVelocity.clone().multiply(getVectorPower());

        Vector addition = new Vector(random.nextDouble() - random.nextDouble(),
                random.nextDouble() - random.nextDouble(),
                random.nextDouble() - random.nextDouble());
        addition.multiply(0.1);

        if (direction.dot(addition) < 0) {
            addition.multiply(-1);
        }

        direction.add(addition);

        shell.setMot(direction.getX(), direction.getY(), direction.getZ());
        nms.addEntity(shell);
        lastFireTime = System.currentTimeMillis();

    }

    @Override
    public synchronized void fire(@Nullable Player shooter) {
      fireBurst();
    }




    @Override
    public ConstructType getType() {
        return ConstructType.HEAVY_MACHINE;
    }


    //the logic works via firing bursts, so within a burst we also need to check whether we
    //can fire single
    @Override
    public boolean canFire() {
        return (getAmmo() > 0 || !requiresReloading()) && !isInvalid() && getCanFire() && !isJammed();
    }

    private boolean canFireSingle() {
        return (getAmmo() > 0 ||!requiresReloading()) && !isJammed();
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
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
