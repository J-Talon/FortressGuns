package me.camm.productions.fortressguns.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFunctional;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block.AllocatorVanillaB;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectHE;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.ItemMergeHandler;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExplosionShellHE extends ExplosionFunctional {

   // private static EffectHE effect = new EffectHE();

    private final Map<Material, List<Tuple2<ItemStack, Block>>> explosionDrops;
    private boolean fullPen;



    public ExplosionShellHE(double x, double y, double z, World world, float radius, @NotNull Entity source, boolean destructive) {
        super(x, y, z, world, radius,destructive, source);
        explosionDrops = new HashMap<>();
    }


    private void getPenetrationExtent(Vector position) {
        double penPower = radius;
        boolean hasPenned = false;

        Vector direction = source.getVelocity().normalize().multiply(0.3);
        Vector lastPosition = position.clone();
        while (penPower > 0) {
            Vector current = lastPosition.clone().add(direction);
            int x,y,z;
            x = current.getBlockX();
            y = current.getBlockY();
            z = current.getBlockZ();

            Block block = world.getBlockAt(x,y,z);
            Material mat = block.getType();
            if (mat.isSolid()) {
                hasPenned = true;
                penPower -= (mat.getBlastResistance() + 0.3F) * 0.3;
            }
            else if (hasPenned) {
                fullPen = true;
                return;
            }

            lastPosition = current;

        }
        fullPen = false;

    }

    @Override
    public void perform() {

        EffectHE effect = new EffectHE();
        Vector position = new Vector(x,y,z);
        getPenetrationExtent(position.clone());

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

                    if (!destructive) {
                        cancel();
                        return;
                    }


                    List<Block> dropped = new ArrayList<>();
                    List<Block> thrown = new ArrayList<>();


                    for (Block block: affectedBlocks) {

                        if (block.getState() instanceof Container) {
                            dropped.add(block);
                            continue;
                        }

                        double res = rand.nextDouble();
                        if (res >= 0.5d) {
                            thrown.add(block);
                        }
                        else {
                            dropped.add(block);
                        }
                    }


                    processDrops(dropped);

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

                    dropItems(explosionDrops);

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

        Vector velocity = source.getVelocity().normalize();
        if (!fullPen) {
            velocity.multiply(-1);
        }

        double dist = loc.distanceSquared(position);
        Vector direction = loc.clone().subtract(position).normalize();

        double magnitude = height * Math.pow(1/(Math.sqrt(Math.PI * width * width)),-(expansion*dist/width));
        double magnitudeVert = Math.max(0,-0.5*dist + 0.5);
        if (magnitude == 0)
            return null;

        direction.multiply(Math.max(-magnitude + 0.3*height,0.2f));
        direction.add(velocity.clone().multiply(magnitude + magnitudeVert));

        Vector random = new Vector(rand.nextDouble() - rand.nextDouble(), rand.nextDouble() - rand.nextDouble(), rand.nextDouble() - rand.nextDouble());
        random.multiply(0.15);
        direction.add(random);

        return direction;
    }


    @Override
    protected void processDrops(Collection<Block> blocks) {

        for (Block next : blocks) {
            Material mat = next.getType();

            if (mat.isAir() || mat == Material.TNT) {
                blockReaction(next);
                continue;
            }

            if (next.getState() instanceof Container cont) {
                Inventory inv = cont.getInventory();
                for (ItemStack stack : inv.getContents()) {
                    insert(explosionDrops, stack, next);
                }
            } else {
                Collection<ItemStack> drops = next.getDrops();
                for (ItemStack stack : drops) {
                    insert(explosionDrops, stack, next);
                }
            }

            next.setType(Material.AIR);
        }
    }


    private void insert(Map<Material, List<Tuple2<ItemStack, Block>>> map, ItemStack stack, Block block) {

        if (stack == null)
            return;

        Material mat = stack.getType();

        if (mat.isAir() || !mat.isItem())
            return;

        if (stack.getAmount() <= 0)
            return;

        Tuple2<ItemStack, Block> tup = new Tuple2<>(stack, block);
        List<Tuple2<ItemStack, Block>> list = map.getOrDefault(mat, null);
        if (list == null) {
            ArrayList<Tuple2<ItemStack, Block>> items = new ArrayList<>();
            items.add(tup);
            map.put(mat, items);
        }
        else {
            list.add(tup);
        }
    }


    @Override
    protected void dropItems(Map<Material, List<Tuple2<ItemStack, Block>>> droppedItems) {

        ItemMergeHandler handler = ItemMergeHandler.getInstance();
        final List<Item> thrownItems = new ArrayList<>();
        for (List<Tuple2<ItemStack, Block>> positions: droppedItems.values()) {
            for (Tuple2<ItemStack, Block> items: positions) {
                Material stackType = items.getA().getType();

                //soooooooo
                //we have an issue with dropping air even though it's dirt
                if (!stackType.isItem() || !stackType.isBlock() || stackType.isAir())
                    continue;

                if (items.getA().getAmount() == 0) {
                    continue;
                }

                Location loc = items.getB().getLocation();
                Item item = world.dropItem(loc, items.getA());
                handler.addTicket(item.getUniqueId());


                Vector vel = getThrowVector(loc.toVector());
                if (vel != null) {
                    item.setVelocity(vel.multiply(2));
                    thrownItems.add(item);
                }
            }
        }

        new BukkitRunnable() {
            final int MAX_ITERS = 60; //magic ooooohhhh
            int iters = 0;
            @Override
            public void run() {

                if (iters >= MAX_ITERS) {
                    cancel();
                    return;
                }
                iters ++;

                for (Item item: thrownItems) {

                    if (item.isOnGround()) {
                        continue;
                    }
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,item.getLocation(),1,0,0,0,0);
                }
            }

            @Override
            public synchronized void cancel() {
                for (Item item: thrownItems) {
                    handler.removeTicket(item.getUniqueId());
                }

                super.cancel();
            }

        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }


    @Override
    public float getMaxDamage() {
        return 30f;
    }


    @Override
    public double damageFalloff(double distanceSquared) {
        double max = getMaxDamage();
        if (max == 0)
            return 0;

        double scale = Math.pow(getRadius(),2) / max;
        return Math.max(0, (-1/scale * distanceSquared) + max);

    }

}
