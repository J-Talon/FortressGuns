package me.camm.productions.fortressguns.Explosion.Explosions;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionShell;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block.AllocatorVanillaB;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectHE;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExplosionShellHE extends ExplosionFG implements ExplosionShell {

   // private static EffectHE effect = new EffectHE();

    public ExplosionShellHE(double x, double y, double z, World world, float radius, @Nullable Entity source, boolean destructive) {
        super(x, y, z, world, radius, source, destructive);
    }

    @Override
    public void perform() {

        EffectHE effect = new EffectHE();
        Vector position = new Vector(x,y,z);

        AllocatorVanillaB vanillaB = new AllocatorVanillaB(world, position);
        List<Block> affectedBlocks;

        affectedBlocks = vanillaB.allocate(radius);
        Collections.shuffle(affectedBlocks, rand);

        final ExplosionFG fg = this;
        new BukkitRunnable() {
            int iters = 0;
            public void run() {

                if (iters > 0) {
                    effect.postMutation(fg);

                    AllocatorVanillaE vanilla = new AllocatorVanillaE(world,position);
                    List<Tuple2<Float, Entity>> entities = vanilla.allocate(new Tuple2<>(radius, source));
                    for (Tuple2<Float, Entity> tup: entities) {
                        damageEntity(tup.getB(), tup.getA());
                    }

                    if (!destroysBlocks)
                        return;

                  //  processDrops(affectedBlocks);

//                    List<Integer> indices = new ArrayList<>();
//                    List<Block> thrown = new ArrayList<>();
//
//                    for (int i = 0; i < affectedBlocks.size(); i ++) {
//                        if (rand.nextFloat() <= 0.2f)
//                            continue;
//                        indices.add(i);
//                    }
//
//                    if (indices.size() > 0) {
//
//                        for (int index: indices) {
//                            thrown.add(affectedBlocks.get(index));
//                        }
//                        affectedBlocks.subList(0, indices.size()).clear();
//                    }
//                    processDrops(affectedBlocks);
//

                    Vector velocity = source.getVelocity().multiply(-1).normalize();


                    int i = 0;
                    for (Block next: affectedBlocks) {
                        Location loc = next.getLocation().add(0.5, 0.5, 0.5);

                        Vector direction = loc.toVector().subtract(position).normalize();
                        double dist = loc.toVector().distanceSquared(position);

                        double width = 0.24;
                        double height = 2.3;
                        double expansion = 0.02;

                        double magnitude = height * Math.pow(1/(Math.sqrt(Math.PI * width * width)),-(expansion*dist/width));
                        double magnitudeVert = Math.max(0,-0.5*dist + 0.5);

                        if (magnitude == 0)
                            continue;

                        direction.multiply(magnitude * 0.2);
                        direction.add(velocity.clone().multiply(magnitude + magnitudeVert));


                        System.out.println(i+"| dist: "+dist+"| direction:"+direction);

                        FallingBlock block = world.spawnFallingBlock(loc, next.getBlockData());
                        next.setType(Material.AIR);
                        block.setHurtEntities(true);

                        block.setVelocity(direction);
                        i++;
                    }


                    cancel();
                }
                else {
                    effect.preMutation(fg,null);
                    iters ++;
                }
            }

        }.runTaskTimer(FortressGuns.getInstance(),0,5);

    }
}
