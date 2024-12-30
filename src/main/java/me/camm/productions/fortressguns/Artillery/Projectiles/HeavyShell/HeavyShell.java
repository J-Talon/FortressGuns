package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;

import me.camm.productions.fortressguns.Util.Explosions.ExplosionHelper;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
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


import javax.annotation.Nullable;


public abstract class HeavyShell extends EntityArrow implements ArtilleryProjectile, ProjectileExplosive {
    protected EntityPlayer shooter;
    org.bukkit.World bukkitWorld;

    private static final ItemStack stack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.IRON_NUGGET));

    public HeavyShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world);

        if (shooter != null) {
            this.shooter = ((CraftPlayer) shooter).getHandle();
            setShooter(this.shooter);
        }

        bukkitWorld = getWorld().getWorld();
        init();
    }



    private void init(){
        this.setCritical(true);
    }



    @Override
    protected ItemStack getItemStack() {
        return stack;
    }

    //these methods are for when the shell hits an entity or block
    @Override
    protected void a(MovingObjectPositionEntity pos) {
        Entity hit = pos.getEntity();

        //av is to do with damage in entity arrow


        Entity shooter = this.getShooter();

        if (!(shooter instanceof EntityHuman)) {
            preHit(pos);
            return;
        }

        DamageSource damageSource = GunSource.gunShot((EntityHuman)shooter, this);
        boolean isEnderman = hit.getEntityType() == EntityTypes.w;

        if (damageSource == null) {
            preHit(pos);
            return;
        }


        if (hit.damageEntity(damageSource, getHitDamage())) {
            if (isEnderman) {
                return;
            }

            if (hit instanceof EntityLiving entityliving) {

                //probably knockback or something.
                if (this.aw > 0) {
                    Vec3D vec3d = this.getMot().d(1.0, 0.0, 1.0).d().a((double) this.aw * 0.6);
                    if (vec3d.g() > 0.0) {
                        entityliving.i(vec3d.b, 0.1, vec3d.d);
                    }
                }

                if (entityliving != shooter && entityliving instanceof EntityHuman && shooter instanceof EntityPlayer && !this.isSilent()) {
                    ((EntityPlayer) shooter).b.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, 0.0F));
                }

                //t.y in class world is if the world is clentside
            }
        }
        preHit(pos);

    }


    @Override
    protected void a(MovingObjectPositionBlock pos) {
       preHit(pos);
    }


    //base explosion
    @Override
    public void preHit(@Nullable MovingObjectPosition pos){

        Vec3D hit;
        if (pos == null) {
            hit = new Vec3D(locX(), locY(), locZ());
        }
        else
            hit = pos.getPos();

        explode(hit);
    }


    public void explode(@Nullable Vec3D hit) {
        this.die();

        if (hit == null)
            ExplosionHelper.heavyShellExplosion(getWorld(),this,locX(),locY(),locZ(), getExplosionPower(),this);
        else
            ExplosionHelper.heavyShellExplosion(getWorld(),this,hit.getX(),hit.getY(),hit.getZ(), getExplosionPower(),this);
    }


    protected void playSound(SoundPlayer sp, double hypotenuse) {

        Location loc = new Location(bukkitWorld,locX(), locY(), locZ());

        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/4) {
            Location next = loc.clone();
            double offset1 = hypotenuse * Math.sin(angle);
            double offset2 = hypotenuse * Math.cos(angle);

            next.add(offset1, 0, offset2);
            sp.playSound(next);
        }
    }



}
