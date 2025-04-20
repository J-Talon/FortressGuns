package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Explosions.Old.ExplosionFactory;
import me.camm.productions.fortressguns.Util.DamageSource.ShellSource;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class StandardHeavyShell extends HeavyShell {


    private static float hitDamage = 10;
    private static float explosionPower = 4;


    public StandardHeavyShell(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, x, y, z, shooter, source);
    }


    public static void setHitDamage(float hitDamage) {
        StandardHeavyShell.hitDamage = hitDamage;
    }

    public static void setExplosionPower(float explosionPower) {
        StandardHeavyShell.explosionPower = explosionPower;
    }

    @Override
    public  float getExplosionPower() {
        double percent = getMot().f() / source.getVectorPower();
        return (float)(explosionPower * percent);
    }

    @Override
    public float getHitDamage() {
        return hitDamage;
    }


    public void explode(@javax.annotation.Nullable Vec3D hit) {
        this.die();
        if (hit == null)
            ExplosionFactory.solidShellExplosion(getWorld(),this,locX(),locY(),locZ(), getExplosionPower());
        else
            ExplosionFactory.solidShellExplosion(getWorld(),this,hit.getX(),hit.getY(),hit.getZ(), getExplosionPower());
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        if (!super.onEntityHit(hitEntity, entityPosition)) {
            return false;
        }

        float length = (float)getMot().f();
        final float MULTIPLIER = 1.7f;

        float damageMultiplier = (float) (length / source.getVectorPower());

        Vec3D velocity = getMot().d().a(MULTIPLIER);  //d() => norm,  a() => mult
        hitEntity.i(velocity.getX() * MULTIPLIER, velocity.getY() * MULTIPLIER,velocity.getZ() * MULTIPLIER);  //i => push()

        EntityDamageSource source = ShellSource.artilleryHit(shooter, this);
        if (hitEntity instanceof EntityLiving living) {
            //if the damage would be blocked
            if (living.applyBlockingModifier(ShellSource.artilleryHit(shooter,this))) {
                hitEntity.damageEntity(source, getHitDamage() * damageMultiplier * 5);
                explode(this.getPositionVector());
                return true;
            }

            if (living != shooter && living instanceof EntityHuman && !this.isSilent()) {
                shooter.b.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, 0.0F));
            }
        }

        hitEntity.damageEntity(source,getHitDamage() * damageMultiplier);
        explode(this.getPositionVector());
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition block) {
        Vec3D normalVector = getNormalVector(blockFace);

        Vec3D motion = getMot();
        double length = motion.f();
        Vec3D motionNorm = motion.d();

        double dotProduct = normalVector.b(motionNorm);

        if (dotProduct > 0) {
            return false;
        }

        dotProduct = Math.abs(dotProduct);

        Block bukkitBlock = bukkitWorld.getBlockAt(block.getX(), block.getY(), block.getZ());
        double angleReflection = Math.min(Math.max(0.03*bukkitBlock.getType().getHardness(),0),0.35);  //function
        boolean energyConserved = length > (0.1f * source.getVectorPower());

        System.out.println("type:"+bukkitBlock.getType()+"hardness:"+bukkitBlock.getType().getHardness());
        if (dotProduct > angleReflection && energyConserved) {
            System.out.println("exploding now: "+dotProduct +"||"+ angleReflection);
            explode(exactHitPosition);
        }
        else {
            System.out.println("ricochet");
            //ricochet
            Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
            bukkitWorld.playSound(loc, Sound.ITEM_TRIDENT_RETURN,2,2);
            bukkitWorld.playSound(loc, Sound.BLOCK_ANVIL_PLACE,2,2);
            bukkitWorld.playSound(loc, Sound.BLOCK_BELL_USE,2,2);
            bukkitWorld.spawnParticle(Particle.END_ROD,loc,10,0,0,0,0.2d);
            bukkitWorld.spawnParticle(Particle.BLOCK_CRACK,loc, 30, 0.1, 0.1, 0.1,1, bukkitBlock.getBlockData());

            if (energyConserved) {
                System.out.println("ricochet success");
                Vec3D reflectionVector = getReflectionVector(blockFace);
                Vec3D result = new Vec3D(motion.getX() * reflectionVector.getX(), motion.getY() * reflectionVector.getY(), motion.getZ() * reflectionVector.getZ());
                setPositionRaw(exactHitPosition.getX(), exactHitPosition.getY(), exactHitPosition.getZ());

                setMot(result);
                return false;
            }

            remove();
        }
        return true;
    }


    public Vec3D getNormalVector(EnumDirection blockFace) {
        BaseBlockPosition pos = blockFace.p();
        return new Vec3D(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vec3D getReflectionVector(EnumDirection blockFace) {
        Vec3D vector = getNormalVector(blockFace);
        double x,y,z;
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();

        return new Vec3D(x == 0 ? 1 : x, y == 0 ? 1: y, z == 0 ? 1: z);

    }

}
