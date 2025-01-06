package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryPart;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;


/**
 * @author CAMM
 */
public class FlakLightShell extends LightShell implements ProjectileExplosive
{


    private static float hitDamage = 10;


    //(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world)
    public FlakLightShell(World world, double x, double y, double z, EntityPlayer human, Artillery source) {
        super(world,x,y,z, human, source);
    }


    public void preHit(MovingObjectPosition hit) {

        CraftWorld bukkit = getWorld().getWorld();

        if (hit == null) {
            bukkit.spawnParticle(Particle.SQUID_INK,locX(), locY(), locZ(),20,0,0,0,0.2);
            explode(null);
        }else {

            if (hit instanceof MovingObjectPositionBlock) {
                bukkit.spawnParticle(Particle.SQUID_INK,locX(), locY(), locZ(),20,0,0,0,0.2);
            }
            else bukkit.spawnParticle(Particle.SQUID_INK,locX(), locY(), locZ(),20,0,0,0,0.2);

            explode(hit.getPos());
        }
        this.die();

    }

    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        FlakLightShell.hitDamage = hitDamage;
    }


    @Override
    public float getExplosionPower() {
        return 1;
    }

    @Override
    public void explode(@Nullable Vec3D hit) {
        DamageSource source = GunSource.gunShot(gunOperator,this);
        World world = getWorld();
        world.createExplosion(this, source,null,locX(), locY(), locZ(),1,false, Explosion.Effect.a);
    }
}
