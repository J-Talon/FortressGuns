package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileType;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile.SectionHMGBullet;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import me.camm.productions.fortressguns.Util.configv2.ConfigurationHolder;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


public class StandardLightShell extends LightShell implements ConfigurationHolder<SectionHMGBullet>, ProjectileType.ISolidLightShell {

    private static float hitDamage = 5;
    private static float weight = 0.1f;
    private static boolean configApplied = false;

    public StandardLightShell(World world, double x, double y, double z, Player human, Artillery source) {
        super(world, x, y, z, human, source);

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
    }


    @Override
    public void setStaticDefaults(@Nullable SectionHMGBullet data) {
        configApplied = true;
        if (data==null) return;

        hitDamage = data.getDamage().floatValue();
        weight = data.getWeight().floatValue();

    }

    @Override
    public boolean onEntityHit(org.bukkit.entity.Entity bukkit, Vector entityPosition) {
        this.a(GameEvent.I, this.getShooter());

        Entity hitEntity = ((CraftEntity)bukkit).getHandle();


        if (shooter != null) {
            DamageSource source = GunSource.gunShot(shooter,this);
            hitEntity.damageEntity(source, getHitDamage());
        }
        else
            hitEntity.damageEntity(DamageSource.n, getHitDamage()); //generic damage

        //hurt ticks I believe this is
        if (hitEntity instanceof EntityLiving) {
            hitEntity.W = 0;
            this.remove();
            return true;
        }

        if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
            this.remove();
            return true;
        }

        if (hitEntity instanceof ProjectileFG || hitEntity instanceof IProjectile) {
            float otherWeight = (hitEntity instanceof ProjectileFG) ? ((ProjectileFG) hitEntity).getWeight() : 0;
            Vec3D thisDeflection = hitEntity.getPositionVector().a(getPositionVector()).d();  // subtract
            Vec3D otherDeflection = thisDeflection.a(-1F); //mult
            otherDeflection = otherDeflection.a(getWeight());
            hitEntity.setMot(hitEntity.getMot().add(otherDeflection.getX(), otherDeflection.getY(), otherDeflection.getZ()));

            thisDeflection = thisDeflection.a(otherWeight);
            setMot(getMot().add(thisDeflection.getX(), thisDeflection.getY(), thisDeflection.getZ()));
            return false;
        }


        org.bukkit.World bukkitWorld = getWorld().getWorld();

        if (bukkit instanceof Explosive) {

            Entity bukkitShooter = null;

            if (bukkit instanceof Projectile) {
                try {
                    bukkitShooter = (Entity)(((Projectile) bukkit).getShooter());
                }
                catch (ClassCastException ignored) {
                }
            }
            bukkitWorld.createExplosion(new Location(bukkitWorld, entityPosition.getX(), entityPosition.getY(), entityPosition.getZ()),
                    ((Explosive) bukkit).getYield(), ((Explosive) bukkit).isIncendiary(),true,
                    (org.bukkit.entity.Entity) bukkitShooter);
            hitEntity.die();
            remove();
            return true;

        }
        remove();
        return true;
    }



    @Override
    public void onWaterEnter() {
        ExplosionFactory.get().smallSplashExplosion(bukkitWorld,locX(), locY(), locZ());
    }

    @Override
    public float getWeight() {
        return weight;
    }

//    public static void setHitDamage(float hitDamage) {
//        StandardLightShell.hitDamage = hitDamage;
//    }
//
//    public static void setWeight(float weight) {
//        StandardLightShell.weight = weight;
//    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }
}
