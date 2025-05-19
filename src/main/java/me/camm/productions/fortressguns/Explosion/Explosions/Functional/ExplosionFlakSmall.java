package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectFlakSmall;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ExplosionFlakSmall extends ExplosionFG {


    public ExplosionFlakSmall(double x, double y, double z, World world, float radius, Entity source) {
        super(x, y, z, world, radius, source, false);
    }

    @Override
    public void perform() {
        //allocate entities
        AllocatorVanillaE allocator = new AllocatorVanillaE(getWorld(),new Vector(x,y,z));
        //allocator.allocate();
        //for each entity => damage via fxn of exposure and distance

        EffectFlakSmall effect = new EffectFlakSmall();
        effect.preMutation(this, null);

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
