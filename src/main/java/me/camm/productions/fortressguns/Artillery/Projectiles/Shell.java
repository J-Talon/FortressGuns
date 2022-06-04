package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Artillery.Projectiles.Modifier.*;

import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;

import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;


import javax.annotation.Nullable;


public class Shell extends EntityArrow {

    private Entity terminus;
    private static final int DISTANCE_CLOSE, DISTANCE_FAR, DAMAGE;

    static {
        DISTANCE_CLOSE = 16;
        DISTANCE_FAR = 400;
        DAMAGE = 35;

    }


    private static final ItemStack stack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.IRON_NUGGET));
    private final ModifierType mod;

    public Shell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable ModifierType mod) {
        super(entitytypes, d0, d1, d2, world);
        this.mod = mod;
        init();
    }

    private void init(){
        this.setDamage(DAMAGE);
        this.setCritical(true);
        this.terminus = null;
    }

    @Override
    protected ItemStack getItemStack() {
        return stack;
    }

    @Override
    public boolean c_() {
        return super.c_();
    }



    //these methods are for when the shell hits an entity or block
    @Override
    protected void a(MovingObjectPositionEntity pos) {
        super.a(pos);
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

        if (pos instanceof MovingObjectPositionEntity) {
            DamageSource source = DamageSource.arrow(this, this);
            Entity hitEntity = ((MovingObjectPositionEntity) pos).getEntity();

            if (hitEntity instanceof LivingEntity)
              hitEntity.damageEntity(source, (float)getDamage());
        }

        if (modifier == null)
        {
            Explosion explosion = getWorld().createExplosion(this ,hitLoc.getX(),hitLoc.getY(),hitLoc.getZ(),4, false, Explosion.Effect.c);
            explosion.a(true);
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

                if (flightTime > 100)
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
                    modifier = new FlakModifier(hit);
                    break;
            }
        }
        return modifier;

    }
}
