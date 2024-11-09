package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.LightShell;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
public class FlakLightShell extends LightShell
{


    //(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world)
    public FlakLightShell(World world, double x, double y, double z, EntityHuman human, Artillery source) {

        super(world,x,y,z, human, source);

    }




    public void preTerminate(MovingObjectPosition hit) {
        DamageSource source = GunSource.gunShot(gunOperator);
        World world = getWorld();
        world.createExplosion(this, source,null,locX(), locY(), locZ(),1,false, Explosion.Effect.a);

        //this.getHandle().sendParticles((EntityPlayer)null, CraftParticle.toNMS(particle, data), x, y, z, count, offsetX, offsetY, offsetZ, extra, force);

        //public CraftWorld(WorldServer world, ChunkGenerator gen, BiomeProvider biomeProvider, Environment env)
        CraftWorld bukkit = world.getWorld();
        bukkit.spawnParticle(Particle.SQUID_INK,locX(), locY(), locZ(),20,0,0,0,0.2);

        ParticleParam var1 = Particles.Y;
        for(int var2 = 0; var2 < 8; ++var2) {
            this.t.addParticle(var1, this.locX(), this.locY(), this.locZ(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public float getDamageStrength() {
        return 0;
    }
}
