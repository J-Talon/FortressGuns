package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileType;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.config.ConfigManager;
import me.camm.productions.fortressguns.Util.config.Wrapper.Projectile.SectionLightFlakBullet;
import me.camm.productions.fortressguns.Util.damage.builder.DamageField;
import me.camm.productions.fortressguns.Util.damage.builder.DamageInfo;
import me.camm.productions.fortressguns.Util.damage.builder.DamageProvider;
import me.camm.productions.fortressguns.Util.damage.source.GunSource;
import me.camm.productions.fortressguns.Util.config.ConfigurationHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


/**
 * @author CAMM
 */
public class FlakLightShell extends LightShell implements ProjectileExplosive, ConfigurationHolder<SectionLightFlakBullet>, ProjectileType.IFlakLightShell
{

    private static float hitDamage = 10;
    private static float weight = 0.015f;
    private static boolean configApplied = false;

    private org.bukkit.entity.Entity ignore;



    //(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world)
    public FlakLightShell(World world, double x, double y, double z, Player human, Artillery source) {
        super(world,x,y,z, human, source);
        ignore = null;

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getLightFlakBullet());
    }

    @Override
    public void setStaticDefaults(@Nullable SectionLightFlakBullet data) {
        configApplied = true;
        if (data==null) return;
        hitDamage = data.getDamage().floatValue();
        weight = data.getWeight().floatValue();


    }

    @Override
    public boolean onEntityHit(Entity bukkit, Vector entityPosition) {

        net.minecraft.world.entity.Entity hitEntity = ((CraftEntity)bukkit).getHandle();

        DamageInfo info = new DamageInfo(DamageField.BULLET,bukkitShooter, this.getBukkitEntity(), getHitDamage());
        DamageProvider.getInstance().accept(bukkit, info);

        double length = this.getMot().f();
        final double FACTOR = 1.5;

        Vector position = CraftVector.toBukkit(getPositionVector());

        if (length == 0) {
            explode(position);
            return true;
        }
        Vec3D motion = this.getMot();
        motion = motion.a(FACTOR/length);

        Vec3D velocity = hitEntity.getMot().e(motion);
        hitEntity.setMot(velocity);
        explode(position);
        return true;
    }

    @Override
    public boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock) {
        super.onBlockHit(exactHitPosition, blockFace, hitBlock);
        explode(exactHitPosition);
        return true;
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
    public float getWeight() {
        return weight;
    }


    @Override
    public void explode(@Nullable Vector hit) {

        double x,y,z;
        if (hit == null) {
            Vec3D pos = getPositionVector();
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }
        else {
            x = hit.getX();
            y = hit.getY();
            z = hit.getZ();
        }

        ExplosionFactory.get().flakLightExplosion(bukkitWorld,getBukkitEntity(), shooter.getBukkitEntity(),x,y,z, ignore);
    }
}
