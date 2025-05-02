package me.camm.productions.fortressguns.Explosion;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionShell;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block.AllocatorVanillaB;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ExplosionShellHE extends ExplosionFG implements ExplosionShell {

   // private static EffectHE effect = new EffectHE();

    public ExplosionShellHE(double x, double y, double z, World world, float radius, @Nullable Entity source, boolean destructive) {
        super(x, y, z, world, radius, source, destructive);
    }

    @Override
    public void perform() {

        Vector position = new Vector(x,y,z);
      //  effect.preMutation(this, null);
        AllocatorVanillaE vanilla = new AllocatorVanillaE(world,position);
        List<Tuple2<Float, Entity>> entities = vanilla.allocate(new Tuple2<>(radius, source));
        for (Tuple2<Float, Entity> tup: entities) {
            damageEntity(tup.getB(), tup.getA());

        }

        if (destroysBlocks) {
            AllocatorVanillaB vanillaB = new AllocatorVanillaB(world, position);
            List<Block> affectedBlocks;

            affectedBlocks = vanillaB.allocate(radius);
            Collections.shuffle(affectedBlocks, rand);

            processDrops(affectedBlocks);
        }

      //  effect.postMutation(this, new EffectContext<>(samples));

    }
}
