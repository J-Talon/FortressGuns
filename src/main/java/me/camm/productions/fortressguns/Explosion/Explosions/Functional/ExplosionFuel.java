package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFunctional;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block.AllocatorVanillaB;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectMissile;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

public class ExplosionFuel extends ExplosionFunctional {


    public ExplosionFuel(double x, double y, double z, World world, float radius, Entity source, boolean destructive) {
        super(x, y, z, world, radius,destructive,source);
    }

    @Override
    public void perform() {

        EffectMissile effect = new EffectMissile();
        effect.preMutation(this,1d);

        AllocatorVanillaE allocator = new AllocatorVanillaE(getWorld(),new Vector(x,y,z));
        List<Tuple2<Float, Entity>> entities = allocator.allocate(new Tuple2<>(radius,null));
        for (Tuple2<Float, Entity> tup: entities) {
            damageEntity(tup.getB(), tup.getA());
        }

        if (!destructive) {
            effect.postMutation(this);
            return;
        }

        AllocatorVanillaB allocatorBlock = new AllocatorVanillaB(getWorld(), new Vector(x,y,z));
        List<Block> blocks = allocatorBlock.allocate(getRadius());
        processDrops(blocks);

        effect.postMutation(this);
    }

    @Override
    public float getMaxDamage() {
        return 25;
    }


    @Override
    public double damageFalloff(double distanceSquared) {
        double max = getMaxDamage();
        if (max == 0)
            return 0;

        double scale = Math.pow(getRadius(),2) / max;
        return Math.max(0, (-1/scale * distanceSquared) + max);


        ///okay you need to get your y scaling in check.
        // the variable max is what controls this.
        //also the damage isn't consistent- especially for the ender dragon.
    }


}
