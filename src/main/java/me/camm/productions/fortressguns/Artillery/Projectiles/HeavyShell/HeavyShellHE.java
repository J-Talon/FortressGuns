package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.configv2.ConfigManager;
import me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile.SectionShellHE;
import me.camm.productions.fortressguns.Util.configv2.ConfigurationHolder;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;




public class HeavyShellHE extends HeavyShell implements ProjectileExplosive, ConfigurationHolder<SectionShellHE> {



    private static float hitDamage = 10;
    private static float explosionPower = 4;
    private static boolean configApplied = false;


//    private Vector spewDir = null;
//    private Location center = null;

    public HeavyShellHE(World world, double d0, double d1, double d2, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, d0, d1, d2, shooter, source);

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded())
            return;
        setStaticDefaults(manager.getShellHE());

    }


    @Override
    public void setStaticDefaults(@Nullable SectionShellHE data) {
        configApplied = true;
        if (data == null) return;
        explosionPower = data.getExplosionPower().floatValue();
        hitDamage = data.getDamage().floatValue();
    }

    @Override
    public float getHitDamage() {
        return hitDamage;
    }


    @Override
    public float getExplosionPower() {
        return explosionPower;
    }


    @Override
    public boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock) {
        explode(exactHitPosition);
        return true;
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vector entityPosition) {
        super.onEntityHit(hitEntity, entityPosition);

        if (hitEntity instanceof ProjectileFG) {
            ((ProjectileFG) hitEntity).remove();
        }

        explode(CraftVector.toBukkit(getPositionVector()));
        return true;
    }

    @Override
    public void explode(@Nullable Vector hit) {

        //we'll worry about this in the future but
        //for a better explosion use a penetration value.
        //so if you trace and you find air, then go forwards. else go backwards.
        if (hit == null)
            ExplosionFactory.get().heavyShellExplosion(bukkitWorld,this.getBukkitEntity(), locX(), locY(), locZ(), getExplosionPower());
        else
            ExplosionFactory.get().heavyShellExplosion(bukkitWorld,this.getBukkitEntity(), hit.getX(), hit.getY(), hit.getZ(), getExplosionPower());
    remove();
    }


    @Override
    public float getWeight() {
        return 0.5F;
    }


}
