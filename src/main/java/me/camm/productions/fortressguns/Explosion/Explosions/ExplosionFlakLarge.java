package me.camm.productions.fortressguns.Explosion.Explosions;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectFlakLarge;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

public class ExplosionFlakLarge extends ExplosionFG {


    public ExplosionFlakLarge(double x, double y, double z, World world, float radius, Entity source) {
        super(x, y, z, world, radius, source, false);
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
}
