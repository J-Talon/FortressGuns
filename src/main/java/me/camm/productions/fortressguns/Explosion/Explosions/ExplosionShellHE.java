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
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExplosionShellHE extends ExplosionFG implements ExplosionShell {

   // private static EffectHE effect = new EffectHE();

    private final List<Item> initialItems;

    public ExplosionShellHE(double x, double y, double z, World world, float radius, @Nullable Entity source, boolean destructive) {
        super(x, y, z, world, radius, source, destructive);
        initialItems = new ArrayList<>();
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


                    List<Integer> indices = new ArrayList<>();
                    List<Block> thrown = new ArrayList<>();

                    for (int i = 0; i < affectedBlocks.size(); i ++) {
                        if (rand.nextFloat() <= 0.2f)
                            continue;
                        indices.add(i);
                    }

                    if (indices.size() > 0) {

                        for (int index: indices) {
                            thrown.add(affectedBlocks.get(index));
                        }
                        affectedBlocks.subList(0, indices.size()).clear();
                    }

                    processDrops(affectedBlocks);

                    for (Block next: thrown) {
                        Location loc = next.getLocation().add(0.5, 0.5, 0.5);
                        Vector result = getThrowVector(loc.toVector());
                        if (result == null)
                            continue;


                        FallingBlock block = world.spawnFallingBlock(loc, next.getBlockData());
                        next.setType(Material.AIR);
                        block.setHurtEntities(true);
                        block.setVelocity(result);
                    }

                    //System.out.println("items size:"+initialItems.size());
                    for (Item item: initialItems) {
                        Location loc = item.getLocation();
                        Vector result = getThrowVector(loc.toVector());
                        if (result == null)
                            continue;
                        item.setVelocity(result);
                    }

                    cancel();
                }
                else {
                    effect.preMutation(fg,new Tuple2<>(1.0,source.getVelocity()));
                    iters ++;
                }
            }

        }.runTaskTimer(FortressGuns.getInstance(),0,5);

    }


    protected @Nullable Vector getThrowVector(Vector loc) {
        final double width = 0.17;
        final double height = 1.3;
        final double expansion = 0.02;

        Vector position = new Vector(x,y,z);

        Vector velocity = source.getVelocity().multiply(-1).normalize();

        double dist = loc.distanceSquared(position);
        Vector direction = loc.clone().subtract(position).normalize();

        double magnitude = height * Math.pow(1/(Math.sqrt(Math.PI * width * width)),-(expansion*dist/width));
        double magnitudeVert = Math.max(0,-0.5*dist + 0.5);
        if (magnitude == 0)
            return null;

        direction.multiply(Math.max(-magnitude + 0.3*height,0.2f));
        direction.add(velocity.clone().multiply(magnitude + magnitudeVert));

        return direction;
    }


    @Override
    protected void dropItems(Map<Material, List<Tuple2<ItemStack, Block>>> droppedItems) {

        for (List<Tuple2<ItemStack, Block>> positions: droppedItems.values()) {
            for (Tuple2<ItemStack, Block> items: positions) {
                Item item = world.dropItem(items.getB().getLocation(), items.getA());
                System.out.println("dropping item");
                initialItems.add(item);
            }
        }
    }

}
