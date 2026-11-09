package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFunctional;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorConeE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectDebris;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.Math.Tuple3;
import me.camm.productions.fortressguns.Util.config.ConfigManager;
import me.camm.productions.fortressguns.Util.config.ConfigurationHolder;
import me.camm.productions.fortressguns.Util.config.Wrapper.Projectile.SectionShellSolid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ExplosionDebris extends ExplosionFunctional implements ConfigurationHolder<SectionShellSolid> {


    private final Vector direction;
    private final Block context;

    private static boolean configApplied = false;
    private static double explosionDamage = 25;
    private static double angle = 0;

    public ExplosionDebris(double x, double y, double z, World world, float radius, Entity source, Vector direction, @Nullable Block context) {
        super(x, y, z, world, radius,false, source);
        this.context = context;

        if (direction.lengthSquared() == 0) {
            this.direction = source.getLocation().getDirection();
        }
        else
            this.direction = direction.clone().normalize();

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getShellSolid());

    }


    @Override
    public void setStaticDefaults(@Nullable SectionShellSolid data) {
        configApplied = true;
        if (data == null) return;
        explosionDamage = data.getExplosionDamage();
        angle = data.getAngle();

    }

    @Override
    public void perform() {


        Vector position = new Vector(x,y,z);

        //pre-mutation
        AllocatorConeE cone = new AllocatorConeE(world, position, angle);
        List<Tuple2<Entity, Float>> affectedEntities = cone.allocate(new Tuple3<>(radius, direction, source));
        //damage entities

        for (Tuple2<Entity, Float> tup: affectedEntities) {
            damageEntity(tup.getA(), tup.getB());
        }

        EffectDebris debris = new EffectDebris();
        debris.preMutation(this, new Tuple2<>(1.0, context));
        debris.preMutation(this, new Tuple2<>(1.0, world.getBlockAt((int)x,(int)y,(int)z)));
        //post mutation
    }


    @Override
    public double getMaxDamage() {
        return explosionDamage;
    }


    @Override
    public double damageFalloff(double distanceSquared) {

        if (distanceSquared == 0)
            return getMaxDamage();

        if (distanceSquared >= radius * radius)
            return 0;

        final double THRESHOLD = 0.01;
        double scale = Math.pow(getRadius(),2) * THRESHOLD;
        double falloff = Math.min(scale * (1/distanceSquared),getMaxDamage());
        if ( falloff > THRESHOLD)
            return falloff;
        else return 0;
    }
}
