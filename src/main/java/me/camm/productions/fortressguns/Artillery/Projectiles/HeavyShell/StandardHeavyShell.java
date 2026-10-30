package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigManager;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile.SectionShellSolid;
import me.camm.productions.fortressguns.Util.DamageSource.ShellSource;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import me.camm.productions.fortressguns.Util.Config.v2.ConfigurationHolder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StandardHeavyShell extends HeavyShell implements ConfigurationHolder<SectionShellSolid> {

    private static final float explosionPower = 2;

    private static float hitDamage = 10;
    private static float weight = 1.2F;
    private static boolean configApplied = false;

    public StandardHeavyShell(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, x, y, z, shooter, source);

        if (configApplied) return;
        ConfigManager manager = ConfigManager.getInstance();
        if (!manager.isLoaded()) return;
        setStaticDefaults(manager.getShellSolid());
    }


    @Override
    public void setStaticDefaults(@Nullable SectionShellSolid data) {
        configApplied = true;
        if (data == null) return;
        hitDamage = data.getDamage().floatValue();
        weight = data.getWeight().floatValue();
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }


    protected void hitEffectBlock(@Nullable Vector hitPos) {
        final float PEN_POWER = 6f;  // put into config

        Vector direction = CraftVector.toBukkit(getMot());

        double penPower = Math.min(source.getVectorPower(), direction.length() / source.getVectorPower()) * PEN_POWER;

        direction.normalize();
        Vector explosionDir = direction.clone();
        direction.multiply(0.3); //resolution


        if (hitPos == null) {
            hitPos = CraftVector.toBukkit(getPositionVector());
        }

        Block currentPen = bukkitWorld.getBlockAt(hitPos.getBlockX(), hitPos.getBlockY(), hitPos.getBlockZ());

        boolean penned = false;
        List<Block> penBlocks = new ArrayList<>();
        Block context = null;

        do {
            Material mat = currentPen.getType();
            float hardness = mat.getHardness();

            if (hardness < 0) {
                penPower = 0;
            }

            context = currentPen;

            if (penPower >= hardness && hardness >= 0) {
                penBlocks.add(currentPen);
                penned = true;
            }

            penPower -= Math.abs(hardness);

            hitPos.add(direction);
            currentPen = bukkitWorld.getBlockAt(hitPos.getBlockX(), hitPos.getBlockY(), hitPos.getBlockZ());
            if (currentPen.getType().isAir())
                break;

        } while (penPower > 0);

        if (!penned) {
            explosionDir.multiply(-1);
        }
        else {
            hitPos.add(direction);
        }

        ExplosionFactory.solidShellExplosion(bukkitWorld, this.getBukkitEntity(),hitPos.getX(), hitPos.getY(), hitPos.getZ(),explosionPower, explosionDir, context);


        for (Block block : penBlocks) {
            block.breakNaturally();
        }


        if (penPower <= 0) {
            this.die();
            return;
        }

        setMot(getMot().a(penPower / PEN_POWER));

    }


    //deflections should only happen if both projectiles are non explosive
    @Override
    public boolean onEntityHit(Entity hitEntity, Vector entityPosition) {

        if (super.onEntityHit(hitEntity, entityPosition)) {
            this.remove();
            return true;
        }

        if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
            this.remove();
            return true;
        }

        net.minecraft.world.entity.Entity hitEntityNMS = ((CraftEntity)hitEntity).getHandle();


        float length = (float)getMot().f();
        float damageMultiplier = (float) (length / source.getVectorPower());
        float damage = getHitDamage();

        Vec3D velocity = getMot().d().a(weight);  //d() => norm,  a() => mult
        hitEntityNMS.i(velocity.getX() * weight, velocity.getY() * weight,velocity.getZ() * weight);  //i => push()

        EntityDamageSource source = ShellSource.artilleryHit(shooter, this);
        if (hitEntityNMS instanceof EntityLiving living) {
            //if the damage would be blocked
            if (living.applyBlockingModifier(ShellSource.artilleryHit(shooter,this))) {
                hitEntityNMS.damageEntity(source, damage * damageMultiplier * 5);
                hitEffectBlock(CraftVector.toBukkit(this.getPositionVector()));
                return true;
            }

            if (living != shooter && living instanceof EntityHuman && !this.isSilent()) {
                shooter.b.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.g, 0.0F));
            }
        }


        hitEntityNMS.damageEntity(source,damage * damageMultiplier);
        Location loc = CraftVector.toBukkit(getPositionVector()).toLocation(bukkitWorld);
        bukkitWorld.playSound(loc,Sound.ITEM_TRIDENT_HIT,1,0);
        bukkitWorld.spawnParticle(Particle.END_ROD,loc,10,0,0,0,0.1f);
        return true;
    }

    @Override
    public boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block block) {

        Vec3D normalVector = CraftVector.toNMS(blockFace.getDirection());

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
        double angleReflection = Math.min(Math.max(0.07*hardness,0),0.35);  //function
        boolean energyConserved = length > (0.1f * source.getVectorPower());

        if (dotProduct > angleReflection && energyConserved) {
            hitEffectBlock(exactHitPosition);
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

                Vector reflectionVector = MathFG.getReflectionVector(blockFace, motion.getX(), motion.getY(), motion.getZ());
                setMot(reflectionVector.getX(), reflectionVector.getY(), reflectionVector.getZ());
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


    public Vec3D getReflectionVector(BlockFace blockFace, Vector in) {
        Vector vector = blockFace.getDirection();
        double x,y,z;
        x = vector.getX() != 0 ? in.getX() * -1 : in.getX();
        y = vector.getY() != 0 ? in.getY() * -1 : in.getY();
        z = vector.getZ() != 0 ? in.getZ() * -1 : in.getZ();
        return new Vec3D(x,y,z);
    }



    @Override
    public float getWeight() {
        return weight;
    }




}
