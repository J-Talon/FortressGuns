package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.network.chat.ChatMessage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class BurningSphere extends EntitySnowball
{

    private final org.bukkit.World world;

    public BurningSphere(World world, double x, double y, double z) {
        super(world, x, y, z);
        setOnFire();
        this.world = world.getWorld();
        this.setCustomName(new ChatMessage("Burning phosphorus"));


    }

    private void setOnFire(){
        this.setOnFire(Integer.MAX_VALUE);
        this.setInvisible(true);
    }

    @SuppressWarnings("deprecation")
    protected void a(MovingObjectPositionEntity position) {
        Entity hit = position.getEntity();

        boolean resistant = false;
        if (hit instanceof EntityLiving) {

            Collection<MobEffect> effects = ((EntityLiving) hit).bR.values();

            if (hit.isFireProof()){
                resistant = true;
            }
            else {
                for (MobEffect effect : effects) {
                    if (PotionEffectType.getById(MobEffectList.getId(effect.getMobEffect())) == PotionEffectType.FIRE_RESISTANCE) {
                        resistant = true;
                        break;
                    }
                }
            }
        }

        int damage = resistant ? 0 : 6;

        Vec3D vector = position.getPos();
        playEffects(vector);
        hit.damageEntity(DamageSource.projectile(this, this.getShooter()),damage);
        hit.setOnFire(300);
    }

    protected void a(MovingObjectPosition var0) {
        super.a(var0);
        if (!this.t.y) {
            Vec3D vector = var0.getPos();
            playEffects(vector);
            this.die();
        }

    }


    private void playEffects(Vec3D vector){
        double x,y,z;
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();

        Location hit = new Location(world, x,y,z);
        Block block = world.getBlockAt((int)vector.getX(),(int)vector.getY(),(int)vector.getZ());
        if (block.getType().isFlammable()) {
            Block air = block.getLocation().add(0,1,0).getBlock();
            if (air.getType().isAir())
                air.setType(Material.FIRE);

        }
        world.spawnParticle(Particle.SMOKE_LARGE,hit,10,0,0,0,0.01);
        world.playSound(hit, Sound.ITEM_FIRECHARGE_USE,1f,1f);




    }
}
