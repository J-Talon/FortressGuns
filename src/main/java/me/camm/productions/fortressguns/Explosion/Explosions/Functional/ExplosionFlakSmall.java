package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFunctional;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectFlakSmall;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import me.camm.productions.fortressguns.Util.Tuple2;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ExplosionFlakSmall extends ExplosionFunctional {

    private final Player shooter;
    private final Entity ignore;

    public ExplosionFlakSmall(double x, double y, double z, World world, float radius, Entity source, Player shooter, @Nullable Entity ignore) {
        super(x, y, z, world,radius, false, source);
        this.shooter = shooter;
        this.ignore = ignore;
    }

    @Override
    public void perform() {
        //allocate entities
        AllocatorVanillaE allocator = new AllocatorVanillaE(getWorld(),new Vector(x,y,z));
        List<Tuple2<Float,Entity>> affected = allocator.allocate(new Tuple2<>(radius, source));

        if (ignore == null) {
            for (Tuple2<Float, Entity> tup: affected) {
                damageEntity(tup.getB(),tup.getA());
            }
        }
        else {
            UUID ignoreId = ignore.getUniqueId();
            for (Tuple2<Float, Entity> tup: affected) {
                Entity ent = tup.getB();
                UUID id = tup.getB().getUniqueId();

                if (ignore.equals(ent) || id.equals(ignoreId)) {
                    continue;
                }

                damageEntity(ent,tup.getA());
            }
        }

        EffectFlakSmall effect = new EffectFlakSmall();
        effect.preMutation(this, null);

    }

    @Override
    protected DamageSource getDamageSource() {

        net.minecraft.world.entity.Entity nmsProj = ((CraftEntity)(source)).getHandle();
        EntityPlayer nmsPlayer = ((CraftPlayer)shooter).getHandle();
        return GunSource.gunShot(nmsPlayer, nmsProj);
    }

    @Override
    public float getMaxDamage() {
        return 10;
    }

    @Override
    public double damageFalloff(double distanceSquared) {
        double max = getMaxDamage();
        if (max == 0)
            return 0;

        double scale = Math.pow(getRadius(),2) / max;
        return Math.max(0, (-1/scale * distanceSquared) + max);
    }
}
