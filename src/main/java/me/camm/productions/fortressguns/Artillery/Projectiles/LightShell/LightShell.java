package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public abstract class LightShell extends ProjectileSnowballFG implements ArtilleryProjectile {

    private static final ItemStack SPRITE = new ItemStack(Items.pD);

    protected Artillery source;



    //world may be null
    public LightShell(@Nullable World world, double x, double y, double z, @Nullable EntityPlayer human, Artillery source) {
        super(world,x,y,z,human);
        this.source = source;
        setItem(SPRITE);
    }

    ///code to allow projectile-projectile collisions
    @Override
    protected boolean a(Entity entity) {

        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
            Entity entity1 = shooter;
            return entity1 == null || !entity1.isSameVehicle(entity);
            //so either the bullet has no shooter, and they're not stacked and not enderman
            //removed the check isInteractable()
        } else {
            return false;
        }
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        this.a(GameEvent.I, this.getShooter());

        if (shooter != null) {
            DamageSource source = GunSource.gunShot(shooter,this);
            hitEntity.damageEntity(source, getHitDamage());
        }
        else
            hitEntity.damageEntity(DamageSource.n, getHitDamage()); //generic damage

        //hurt ticks I believe this is
        if (hitEntity instanceof EntityLiving) {
            hitEntity.W = 0;
        }
        else  if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
        }
        else {
            org.bukkit.entity.Entity bukkit = hitEntity.getBukkitEntity();
            org.bukkit.World bukkitWorld = getWorld().getWorld();

            if (bukkit instanceof Explosive) {

                Entity bukkitShooter = null;

                if (bukkit instanceof Projectile) {
                    try {
                        bukkitShooter = (Entity)(((Projectile) bukkit).getShooter());
                    }
                    catch (ClassCastException ignored) {
                    }
                }
                bukkitWorld.createExplosion(new Location(bukkitWorld, entityPosition.getX(), entityPosition.getY(), entityPosition.getZ()),
                        ((Explosive) bukkit).getYield(), ((Explosive) bukkit).isIncendiary(),true,
                        (org.bukkit.entity.Entity) bukkitShooter);
            }

            hitEntity.die();
        }
        remove();
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        this.a(GameEvent.I, this.getShooter());

        org.bukkit.World bukkit = getWorld().getWorld();
        Block block = bukkit.getBlockAt(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ());

        Material mat = block.getType();
        Collection<org.bukkit.inventory.ItemStack> drops = block.getDrops();
        for (org.bukkit.inventory.ItemStack item: drops)
        {
            Material itemMat = item.getType();
            if (itemMat.isBlock() && (!itemMat.isAir())) {
                mat = itemMat;
                break;
            }
        }

        Vec3D vec = exactHitPosition;
        Vec3D mot = getMot().e().d().a(0.7);
        //0.7 is an arbitrary artistic decision
        //we're basically normalizing, reversing the vector and multiplying it by 0.7
        //to determine where we should spawn particles for an effect

        vec = vec.e(mot);
        Location effectLoc = new Location(bukkit, vec.getX(), vec.getY(), vec.getZ());

        if (!mat.isAir()) {


            bukkit.spawnParticle(Particle.BLOCK_CRACK,effectLoc, 30, 0.1, 0.1, 0.1,1, block.getBlockData());

            net.minecraft.world.level.block.Block nms = CraftMagicNumbers.getBlock(mat);


            int color = nms.s().al;
            if (color == 0) {
                color = 12895428; // light gray color rgb(196, 196, 196)
            }

            Particle.DustOptions options = new Particle.DustOptions(org.bukkit.Color.fromRGB(color),1);
            bukkit.spawnParticle(Particle.REDSTONE,effectLoc, 17,0.3,0.3,0.3,1,options);
        }

        if (!ExplosionFactory.allowDestructiveExplosions()) {
            bukkit.playSound(effectLoc, Sound.BLOCK_WET_GRASS_BREAK,1,0);
            return true;
        }

        if (mat == Material.TNT) {
            block.setType(Material.AIR);
            TNTPrimed primed = bukkit.spawn(new Location(bukkit, hitBlock.getX() + 0.5, hitBlock.getY() + 0.5, hitBlock.getZ() + 0.5),
                    TNTPrimed.class);

            primed.setFuseTicks(0);
        }
        else {
            float hardness = mat.getHardness();
            if (hardness < Material.DIRT.getHardness() && hardness >= 0) {
                block.breakNaturally();
            }
        }

        bukkit.playSound(effectLoc, Sound.BLOCK_WET_GRASS_BREAK,1,0);
        return true;

    }



}
