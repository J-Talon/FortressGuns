package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
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

import java.util.List;

public class ExplosionFlakSmall extends ExplosionFG {

    private final Player shooter;

    public ExplosionFlakSmall(double x, double y, double z, World world, float radius, Entity source, Player shooter) {
        super(x, y, z, world, radius, source, false);
        this.shooter = shooter;
    }

    @Override
    public void perform() {
        //allocate entities
        AllocatorVanillaE allocator = new AllocatorVanillaE(getWorld(),new Vector(x,y,z));
        List<Tuple2<Float,Entity>> affected = allocator.allocate(new Tuple2<>(radius, source));
        for (Tuple2<Float, Entity> tup: affected) {
            damageEntity(tup.getB(),tup.getA());
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
    public double getFalloff(double distanceSquared) {
        double max = getMaxDamage();
        if (max == 0)
            return 0;

        double scale = Math.pow(getRadius(),2) / max;
        return Math.max(0, (-1/scale * distanceSquared) + max);
    }
}
