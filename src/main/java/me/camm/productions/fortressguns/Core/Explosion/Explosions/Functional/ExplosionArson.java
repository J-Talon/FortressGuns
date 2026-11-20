package me.camm.productions.fortressguns.Core.Explosion.Explosions.Functional;

import me.camm.productions.fortressguns.Core.Explosion.Abstract.ExplosionFunctional;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import me.camm.productions.fortressguns.FortressGuns;

public class ExplosionArson extends ExplosionFunctional {

    private boolean penetrate;
    public ExplosionArson (double x, double y, double z, World world, float radius, Entity source, boolean destructive, boolean penetrate) {
        super(x, y, z, world, radius, destructive, source);
        this.penetrate = penetrate;
    }

    private boolean hasLineOfSight(Location from, Location to) {
        Vector direction = to.toVector().subtract(from.toVector());
        double distance = direction.length();

        if (distance < 1.0E-6)
            return true;

        RayTraceResult result = world.rayTraceBlocks(
                from,
                direction.normalize(),
                distance,
                FluidCollisionMode.ALWAYS,
                true
        );

        return result == null || result.getHitBlock() == null;
    }

    @Override
    public void perform() {
        Location impact = new Location(world, x, y, z);
        for (int x = (int) -radius; x <= radius; x++) {
            for (int y = (int) -radius; y <= radius; y++) {
                for (int z = (int) -radius; z <= radius; z++) {
                    // sphere
                    if (x * x + y * y + z * z > radius * radius)
                        continue;

                    Location fireLocation = impact.clone().add(x, y, z);

                    if (!fireLocation.getBlock().getType().isAir())
                        continue;

//                    Location below = fireLocation.clone().subtract(0, 1, 0);

                    if (!penetrate && !hasLineOfSight(fireLocation, impact)) {
                        continue;
                    }

                    Block fireBlock = fireLocation.getBlock();

                    if (!fireBlock.getType().isAir())
                        continue;

                    Fire fireData = (Fire) Bukkit.createBlockData(Material.FIRE);

                    BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP};

                    boolean hasSide = false;

                    for (BlockFace face : faces) {
                        boolean touching = fireBlock.getRelative(face).getType().isSolid();

                        fireData.setFace(face, touching);

                        if (touching)
                            hasSide = true;
                    }

                    if (hasSide) {
                        fireData.setAge(0);
                        fireBlock.setBlockData(fireData, false);
                        Bukkit.getScheduler().runTaskLater(
                                FortressGuns.getInstance(),
                                () -> {
                                    if (fireBlock.getType() == Material.FIRE) {
                                        fireBlock.setType(Material.AIR);
                                    }
                                },
                                20L * 15
                        );
                    } else if (fireBlock.getRelative(BlockFace.DOWN).getType().isSolid()) {
                        fireBlock.setType(Material.FIRE);
                    }
                }
            }
        }
        // entity check
        for (Entity entity : world.getNearbyEntities(impact, radius, radius, radius)) {
            if (entity.isDead())
                continue;
            Location entityCenter = entity.getLocation().clone()
                    .add(0, entity.getHeight() / 2.0, 0);

            // Enforce spherical radius
            if (entityCenter.distanceSquared(impact) > radius * radius)
                continue;

            // Respect line of sight
            if (!penetrate && !hasLineOfSight(impact, entityCenter))
                continue;

            // Ignite for at least 10 seconds
            entity.setFireTicks(Math.max(entity.getFireTicks(), 200));
        }
    }

    @Override
    public double getMaxDamage() {
        return 25;
    }


    @Override
    public double damageFalloff(double distanceSquared) {
        if (distanceSquared >= radius * radius) return 0;

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
