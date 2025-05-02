package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.Old.ExplosionFactory;
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.util.Vector;
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
    public float getHitDamage() {
        return hitDamage;
    }


    public void hitEffect(@Nullable Vec3D hit) {
        final float PEN_POWER = 4.5f;  // put into config

        Vector direction = CraftVector.toBukkit(getMot());

        double penPower = Math.min(source.getVectorPower(), direction.length() / source.getVectorPower()) * PEN_POWER;

        direction.normalize();
        Vector explosionDir = direction.clone();
        direction.multiply(0.3); //resolution


        Vector hitPos;
        if (hit == null) {
            hitPos = CraftVector.toBukkit(getPositionVector());
        }
        else hitPos = CraftVector.toBukkit(hit);

        Block currentPen = bukkitWorld.getBlockAt(hitPos.getBlockX(), hitPos.getBlockY(), hitPos.getBlockZ());

        boolean penned = false;
        while (penPower > 0 && !penned && !currentPen.getType().isAir()) {
            Material mat = currentPen.getType();
            float hardness = mat.getHardness();

            if (penPower >= hardness) {
                currentPen.breakNaturally();
                penned = true;
            }
            penPower -= hardness;
            hitPos.add(direction);
        }

        if (!penned) {
            explosionDir.multiply(-1);
        }

        ExplosionFactory.solidShellExplosion(bukkitWorld, this.getBukkitEntity(),hitPos.getX(), hitPos.getY(), hitPos.getZ(),explosionPower, explosionDir);


        this.die();
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        if (!super.onEntityHit(hitEntity, entityPosition)) {
            return false;
        }

        if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
            this.remove();
            return true;
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
                hitEffect(this.getPositionVector());
                return true;
            }

            if (living != shooter && living instanceof EntityHuman && !this.isSilent()) {
                shooter.b.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, 0.0F));
            }
        }

        hitEntity.damageEntity(source,getHitDamage() * damageMultiplier);
        hitEffect(this.getPositionVector());
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition block) {

        // Vec3D exactHitPosition = hitBlock.getPos();

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

        double hardness = bukkitBlock.getType().getHardness();
        hardness = hardness > 0 ? hardness : Material.OBSIDIAN.getHardness();
        double angleReflection = Math.min(Math.max(0.03*hardness,0),0.35);  //function
        boolean energyConserved = length > (0.1f * source.getVectorPower());

        if (dotProduct > angleReflection && energyConserved) {
            hitEffect(exactHitPosition);
            return true;
        }
        else {

            Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
            if (energyConserved) {
                //ricochet
                bukkitWorld.playSound(loc, Sound.ITEM_TRIDENT_RETURN,2,2);
                bukkitWorld.playSound(loc, Sound.BLOCK_ANVIL_PLACE,2,2);
                bukkitWorld.playSound(loc, Sound.BLOCK_BELL_USE,2,2);
                bukkitWorld.spawnParticle(Particle.END_ROD,loc,10,0,0,0,0.2d);
                bukkitWorld.spawnParticle(Particle.BLOCK_CRACK,loc, 30, 0.1, 0.1, 0.1,1, bukkitBlock.getBlockData());
                Vec3D reflectionVector = getReflectionVector(blockFace, motion);
                setMot(reflectionVector);
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

    public Vec3D getReflectionVector(EnumDirection blockFace, Vec3D in) {
        Vec3D vector = getNormalVector(blockFace);
        double x,y,z;
        x = vector.getX() != 0 ? in.getX() * -1 : in.getX();
        y = vector.getY() != 0 ? in.getY() * -1 : in.getY();
        z = vector.getZ() != 0 ? in.getZ() * -1 : in.getZ();
        return new Vec3D(x,y,z);

    }


    @Override
    public float getWeight() {
        return 1.2F;
    }




}
