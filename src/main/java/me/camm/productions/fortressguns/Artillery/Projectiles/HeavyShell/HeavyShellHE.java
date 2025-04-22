package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosions.Old.ExplosionFactory;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;




public class HeavyShellHE extends HeavyShell implements ProjectileExplosive {



    private static float hitDamage = 10;
    private static float explosionPower = 4;


//    private Vector spewDir = null;
//    private Location center = null;

    public HeavyShellHE(World world, double d0, double d1, double d2, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, d0, d1, d2, shooter, source);
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        HeavyShellHE.hitDamage = hitDamage;
    }

    @Override
    public float getExplosionPower() {
        return explosionPower;
    }

    public static void setExplosionPower(float explosionPower) {
        HeavyShellHE.explosionPower = explosionPower;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        explode(exactHitPosition);
        return true;
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        if (hitEntity instanceof ArtilleryProjectile) {
            ((ArtilleryProjectile) hitEntity).remove();
        }

        explode(getPositionVector());
        return true;
    }

    @Override
    public void explode(@Nullable Vec3D hit) {
        if (hit == null)
            ExplosionFactory.heavyShellExplosion(getWorld(),this, locX(), locY(), locZ(), getExplosionPower(), this);
        else
            ExplosionFactory.heavyShellExplosion(getWorld(),this, hit.getX(), hit.getY(), hit.getZ(), getExplosionPower(), this);
    remove();
    }


    @Override
    public float getWeight() {
        return 0.5F;
    }


    @Override
    public void remove() {
        explode(getPositionVector());
    }


    //    public void preHit(@Nullable MovingObjectPosition pos) {
//
//        if (pos == null) {
//            super.preHit((MovingObjectPosition) null);
//            return;
//        }
//
//        Vec3D backstep = stepBack(pos,this);
//
//        Vec3D currentPos = this.getPositionVector();
//
//        Vec3D blastDir = currentPos.a(backstep);  ///currentPos - hitLoc
//        blastDir = blastDir.d().e();
//
//
//        RayTraceResult centerPoint = bukkitWorld.rayTraceBlocks(
//                new Location(bukkitWorld, currentPos.getX(), currentPos.getY(), currentPos.getZ())
//                , new Vector(blastDir.getX(), blastDir.getY(), blastDir.getZ()),
//                10,
//                FluidCollisionMode.NEVER);
//
//        if (centerPoint == null) {
//            super.preHit(pos);
//            return;
//        }
//
//        Block bukkitBlock = centerPoint.getHitBlock();
//        BlockFace face = centerPoint.getHitBlockFace();
//
//        if (bukkitBlock == null || face == null) {
//            super.preHit(pos);
//            return;
//        }
//
//        spewDir = face.getDirection();
//        center = centerPoint.getHitPosition().add(spewDir.clone().multiply(-3)).toLocation(bukkitWorld);
//        super.preHit(pos);
//    }


//    public void modifyExplosion(ShellExplosion explosion) {
//
//        if (spewDir == null) {
//            return;
//        }
//
//        double SQRT_PI = Math.sqrt(Math.PI);
//
//        List<Block> blocks = explosion.getBukkitBlocksBroken();
//
//        for (Block bukkitBlock: blocks) {
//
//            Location loc = bukkitBlock.getLocation();
//            double xSquared = loc.distanceSquared(center);
//            double magnitude = (1 / 1.3 * SQRT_PI) * Math.pow(Math.E,-0.2f * xSquared) - (0.003 * xSquared) + 0.3;
//            if (magnitude <= 0.1f) {
//                continue;
//            }
//
//            if (bukkitBlock.getState() instanceof Container) {
//                continue;
//            }
//
//            Collection<ItemStack> drops = bukkitBlock.getDrops();
//            if (drops.isEmpty())
//                continue;
//
//            IBlockData res = null;
//            boolean dropsItems = false;
//
//            for (ItemStack stack: drops) {
//                Material next = stack.getType();
//                if (next.isBlock() && !next.isAir() && next.getBlastResistance() > 0) {
//                    net.minecraft.world.level.block.Block nms = CraftMagicNumbers.getBlock(next);
//                    res = nms.getBlockData();
//                    break;
//                }
//                else if (next.isItem()) {
//                    dropsItems = true;
//                }
//            }
//
//            if (dropsItems || res == null)
//                continue;
//
//
//            Vector direction = loc.clone().subtract(center).toVector();
//            Vector velocity = direction.add(spewDir).normalize();
//            velocity.multiply(magnitude);
//
//            EntityFallingBlock entity = new EntityFallingBlock(getWorld(),loc.getX(), loc.getY(),loc.getZ(),res);
//            entity.b = 1;
//            getWorld().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
//            entity.setMot(CraftVector.toNMS(velocity));
//
//
//        }
//    }
}
