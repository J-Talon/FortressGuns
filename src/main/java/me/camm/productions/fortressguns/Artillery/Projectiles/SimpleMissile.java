package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class SimpleMissile extends EntityArrow implements ArtilleryProjectile {

    private final Player shooter;
    private Entity target;
    private org.bukkit.World bukkitWorld;
    private Construct source;



    private int timeDisengaged;
    private int fuelUsed;
    private Vec3D direction;


    private static final double ACCELERATION = 0.01;
    private static final double MAX_SPEED_SQUARED = 9;
    private static final double MAX_SPEED;


    private static final double LOCK_THRESHOLD = 0;
    private static final int FUEL = 400;  // 20 sec

    private static final int DISENGAGE_TIME = 60; //3 sec
    private static final ItemStack item;
    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);
        MAX_SPEED = Math.sqrt(MAX_SPEED_SQUARED);
    }

    public SimpleMissile(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter, Construct source) {
        super(entitytypes, x, y, z, world);
        this.shooter = shooter;
        timeDisengaged = 0;
        fuelUsed = 0;
        bukkitWorld = world.getWorld();
        direction = null;
        this.source = source;
        this.setNoGravity(true);
    }


    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void explode(@Nullable MovingObjectPosition pos) {
        //explode the thing here
        if (pos == null)
            return;

        System.out.println(pos.getClass().toString());

        if (pos instanceof MovingObjectPositionBlock) {
            BlockPosition blockPos = ((MovingObjectPositionBlock) pos).getBlockPosition();
            Block b = bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (b.getType().isAir())
                return;
        }



        if (!(pos instanceof MovingObjectPositionEntity)) {
            playExplosionEffects(pos);
            System.out.println("1");
            return;
        }

        MovingObjectPositionEntity hit = (MovingObjectPositionEntity) pos;
        net.minecraft.world.entity.Entity hitEntity = hit.getEntity();


        if (!(hitEntity instanceof Component)) {
            playExplosionEffects(pos);
            System.out.println("2");
            return;
        }

        Component body = (Component)hitEntity;
        if (!source.equals(body.getBody())) {
            playExplosionEffects(pos);
            System.out.println("3");
        }


    }


    private void playExplosionEffects(MovingObjectPosition pos) {
        System.out.println("die");
        this.die();
    }


    @Override
    public void a(MovingObjectPosition pos) {
    explode(pos);
    }


    public void tick() {

        //unfinished
        super.tick();

        System.out.println("tick");

        if (fuelUsed >= FUEL) {
            this.setNoGravity(false);
            return;
        }

        fuelUsed ++;

        Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,loc,10,0,0,0,0.3);
        bukkitWorld.spawnParticle(Particle.FLAME,loc,10,0,0,0,0.3);


        if (target == null) {

            if (direction == null) {
                direction = getMot().d();
            }
            flyNormally();

        }
        else {
            flyToTarget();
            direction = null;
        }


    }

    public void flyNormally() {
        Vec3D motion = getMot();
        double magnitudeSquared = motion.g();
        if (magnitudeSquared < MAX_SPEED_SQUARED) {
            Vec3D motionMultiplied = direction.a(Math.sqrt(magnitudeSquared) + ACCELERATION);
            setMot(motionMultiplied);
        }
        else setMot(direction.a(MAX_SPEED));
    }


    public void flyToTarget() {

    }



    @Override
    protected ItemStack getItemStack() {
        return item;
    }
}
