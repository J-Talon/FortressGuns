package me.camm.productions.fortressguns.Artillery.Projectiles;


import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.*;

import me.camm.productions.fortressguns.DamageSource.GunSource;
import me.camm.productions.fortressguns.FortressGuns;

import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;

import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;


import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;


import javax.annotation.Nullable;


public class Shell extends EntityArrow {

    private Entity terminus;
    private int flyTime;
    private static final int DISTANCE_CLOSE, DISTANCE_FAR, DAMAGE, DEFAULT_TIME_FLIGHT;

    static {
        DISTANCE_CLOSE = 16;
        DISTANCE_FAR = 400;
        DAMAGE = 35;
        DEFAULT_TIME_FLIGHT = 100;

    }


    private static final ItemStack stack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.IRON_NUGGET));
    private final ModifierType mod;
    private final Player shooter;

    public Shell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable ModifierType mod, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world);
        this.mod = mod;
        this.shooter = shooter;
        init();
    }



    private void init(){
        this.setDamage(DAMAGE);
        this.setCritical(true);
        this.terminus = null;

        flyTime = DEFAULT_TIME_FLIGHT;
    }

    @Override
    protected ItemStack getItemStack() {
        return stack;
    }


    //returns a value that is boolean. Is this the isCritical() method?
    @Override
    public boolean c_() {
        return super.c_();
    }



    //these methods are for when the shell hits an entity or block
    @Override
    protected void a(MovingObjectPositionEntity pos) {
        Entity hit = pos.getEntity();

        //av is to do with damage in entity arrow


        Entity shooter = this.getShooter();




        if (!(shooter instanceof EntityHuman)) {
            explode(pos);
            return;
        }

        DamageSource damageSource = GunSource.gunShot((EntityHuman)shooter);


        boolean isEnderman = hit.getEntityType() == EntityTypes.w;

        if (damageSource == null) {
            explode(pos);
            return;
        }


        if (hit.damageEntity(damageSource, DAMAGE)) {
            if (isEnderman) {
                return;
            }

            if (hit instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) hit;

                //probably knockback or something.
                if (this.aw > 0) {
                    Vec3D vec3d = this.getMot().d(1.0, 0.0, 1.0).d().a((double) this.aw * 0.6);
                    if (vec3d.g() > 0.0) {
                        entityliving.i(vec3d.b, 0.1, vec3d.d);
                    }
                }


                EnchantmentManager.a(entityliving, shooter);
                EnchantmentManager.b((EntityLiving) shooter, entityliving);


                if (entityliving != shooter && entityliving instanceof EntityHuman && shooter instanceof EntityPlayer && !this.isSilent()) {
                    ((EntityPlayer) shooter).b.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, 0.0F));
                }

                //t.y in class world is if the world is clentside

            }

        }
        explode(pos);

    }

    @Override
    protected void a(MovingObjectPositionBlock pos) {
    explode(pos);
    }


    public void explode(MovingObjectPosition pos){


        IModifier modifier;
        org.bukkit.World world = this.getWorld().getWorld();

        Vec3D vec3d = pos.getPos().a(this.locX(), this.locY(), this.locZ());
        Vec3D vec3d1 = vec3d.d().a(0.05000000074505806D);
        Vec3D hitLoc = new Vec3D(this.locX() - vec3d1.b, this.locY() - vec3d1.c, this.locZ() - vec3d1.d);

        Location hit = new Location(world, hitLoc.getX(),hitLoc.getY(),hitLoc.getZ());

        modifier = getModifier(mod, hit);
        this.die();



        if (modifier == null)
        {
            Explosion explosion = getWorld().createExplosion(this ,hitLoc.getX(),hitLoc.getY(),hitLoc.getZ(),4, false, Explosion.Effect.c);
            if (t.getGameRules().getBoolean(GameRules.c)) {
                explosion.a(true);
            }

            explosion.a();
        }
        else
           modifier.activate();

    }

    public void setTerminus(Entity target){
        this.terminus = target;

    }

    public void flyAsFlak(){
        if (this.mod != ModifierType.FLAK)
            return;

        if (terminus == null || terminus.isRemoved() || !terminus.isAlive())
            return;

        final double height = terminus.v;
        final Shell shell = this;

        new BukkitRunnable(){

            int flightTime = 0;
            @Override
            public void run() {

                if (!shell.isAlive() || shell.isRemoved())
                {
                    cancel();
                    return;
                }

                if (v >= height) {

                    //u,v,w
                    double distance2DSquared = (u*u + v*v)-(terminus.u*terminus.u +terminus.v*terminus.v);


                    //so if the target is really close, or we're so far that we're gonna miss, then explode
                    if (distance2DSquared < DISTANCE_CLOSE || distance2DSquared > DISTANCE_FAR) {
                        shell.explodePrematurely();
                        cancel();
                        return;
                    }
                }

                if (flightTime > flyTime)
                {
                    shell.explodePrematurely();
                    cancel();
                    return;
                }

                flightTime ++;

            }
        }.runTaskTimer(FortressGuns.getInstance(),0,2);
    }

    public void explodePrematurely(){
        IModifier modifier;
        org.bukkit.World world = this.getWorld().getWorld();
        Location hit = new Location(world, u,v,w);

        modifier = getModifier(mod, hit);
        this.die();

        if (modifier == null)
        {
            Explosion explosion = getWorld().createExplosion(this ,u,v,w,4, false, Explosion.Effect.c);
            explosion.a(true);
        }
        else
            modifier.activate();
    }



    private @Nullable IModifier getModifier(@Nullable ModifierType type, Location hit){
        if (type == null)
            return null;

        IModifier modifier = null;

        if (mod != null)
        {
            switch (mod) {
                case EXPLOSIVE:
                    modifier = new HighExplosive(hit);
                    break;

                case INCENDIARY:
                    modifier = new Incendiary(hit);
                    getWorld().createExplosion(this,u,v,w,4,true, Explosion.Effect.a);
                    break;

                case FLAK:
                    modifier = new FlakModifier(hit, shooter == null? null: ((CraftPlayer)shooter).getHandle());
                    break;
            }
        }
        return modifier;

    }
}
