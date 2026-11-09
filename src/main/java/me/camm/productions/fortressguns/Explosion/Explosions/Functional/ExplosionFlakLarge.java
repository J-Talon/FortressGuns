package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFunctional;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectFlakLarge;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.config.ConfigManager;
import me.camm.productions.fortressguns.Util.config.ConfigurationHolder;
import me.camm.productions.fortressguns.Util.config.Wrapper.Projectile.SectionFlak;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplosionFlakLarge extends ExplosionFunctional implements ConfigurationHolder<SectionFlak> {

    private static boolean configApplied = false;
    private static double maxDamage = 20f;

    public ExplosionFlakLarge(double x, double y, double z, World world, float radius, Entity source) {
        super(x, y, z, world,radius, false, source);

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        setStaticDefaults(manager.getFlakShell());
    }

    @Override
    public void setStaticDefaults(@Nullable SectionFlak data) {
        configApplied = true;
        if (data == null) return;
        maxDamage = data.getExplosionDamage();
    }

    @Override
    public void perform() {
        AllocatorVanillaE allocatorE = new AllocatorVanillaE(world, new Vector(x,y,z));
        List<Tuple2<Float,Entity>> entities = allocatorE.allocate(new Tuple2<>(radius,source));

        EffectFlakLarge effect = new EffectFlakLarge();
        effect.preMutation(this, null);

        for (Tuple2<Float, Entity> tup: entities) {
            damageEntity(tup.getB(), tup.getA());
        }
    }

    @Override
    public double getMaxDamage() {
        return maxDamage;
    }

    @Override
    public double damageFalloff(double distanceSquared) {
        if (distanceSquared >= radius * radius) return 0;

        double max = getMaxDamage();
        if (max == 0)
            return 0;

        double scale = Math.pow(getRadius(),2) / max;
        return Math.max(0, (-1/scale * distanceSquared) + max);

    }

}
