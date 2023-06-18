package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.DamageSource.GunSource;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;


/**
 * @author CAMM
 */
public class LightFlakShell extends EntitySnowball
{

    private final EntityHuman gunOperator;

    //(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world)
    public LightFlakShell(World world, double x, double y, double z, EntityHuman human) {

        super(world,x,y,z);
        gunOperator = human;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.sy;
    }




    @Override
    protected void a(MovingObjectPositionEntity position) {


        DamageSource source = GunSource.gunShot(gunOperator);
        Entity hit = position.getEntity();
        hit.damageEntity(source, 24);

        //hurt ticks I believe this is
        if (hit instanceof EntityLiving)
            hit.W = 0;


    }

    @Override
    protected void a(MovingObjectPosition var0) {

        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = var0.getType();
        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.c) {
            this.a((MovingObjectPositionEntity)var0);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.b) {
            this.a((MovingObjectPositionBlock)var0);
        }

        if (movingobjectposition_enummovingobjecttype != MovingObjectPosition.EnumMovingObjectType.a) {
            this.a(GameEvent.I, this.getShooter());
        }


        DamageSource source = GunSource.gunShot(gunOperator);
        World world = getWorld();
        world.createExplosion(this, source,null,locX(), locY(), locZ(),1,false, Explosion.Effect.a);

        //this.getHandle().sendParticles((EntityPlayer)null, CraftParticle.toNMS(particle, data), x, y, z, count, offsetX, offsetY, offsetZ, extra, force);

        //public CraftWorld(WorldServer world, ChunkGenerator gen, BiomeProvider biomeProvider, Environment env)
        CraftWorld bukkit = world.getWorld();
        bukkit.spawnParticle(Particle.SQUID_INK,locX(), locY(), locZ(),20,0,0,0,0.2);


            this.a((byte)0);
            this.die();
    }

    @Override
    public void a(byte var0) {

        ParticleParam var1 = Particles.Y;
        for(int var2 = 0; var2 < 8; ++var2) {
            this.t.addParticle(var1, this.locX(), this.locY(), this.locZ(), 0.0D, 0.0D, 0.0D);
        }

    }




}
