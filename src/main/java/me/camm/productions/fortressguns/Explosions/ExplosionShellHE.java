package me.camm.productions.fortressguns.Explosions;

import me.camm.productions.fortressguns.Explosions.Abstract.EffectContext;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionShell;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block.AllocatorVanillaB;
import me.camm.productions.fortressguns.Explosions.AllocatorFunction.Entity.AllocatorVanillaE;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExplosionShellHE extends ExplosionFG implements ExplosionShell {

   // private static EffectHE effect = new EffectHE();

    public ExplosionShellHE(double x, double y, double z, World world, float radius, @Nullable Entity source, boolean destructive) {
        super(x, y, z, world, radius, source, destructive);
    }

    @Override
    public void perform() {

        Vec3D position = new Vec3D(x,y,z);
      //  effect.preMutation(this, null);
        AllocatorVanillaE vanilla = new AllocatorVanillaE(world,position);
        List<Entity> entities = vanilla.allocate(new Tuple<>(radius, source));
        for (Entity e: entities) {
            damageEntity(e);
        }

//        List<Material> samples = new ArrayList<>();
//        int MAX_SAMPLES = 20;


        if (destroysBlocks) {
            AllocatorVanillaB vanillaB = new AllocatorVanillaB(world, position);
            List<BlockPosition> affectedBlocks;

            affectedBlocks = vanillaB.allocate(radius);
            Collections.shuffle(affectedBlocks, rand);
//            for (int i = 0; i < MAX_SAMPLES && i < affectedBlocks.size(); i ++) {
//
//              //  samples.add(i, affectedBlocks.get(i));
//            }

            processBlocks(affectedBlocks);
        }

      //  effect.postMutation(this, new EffectContext<>(samples));

    }
}
