package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BurningSphere extends EntitySnowball
{

    private final org.bukkit.World world;

    public BurningSphere(World world, double x, double y, double z) {
        super(world, x, y, z);
        setOnFire();
        this.world = world.getWorld();
        this.setCustomName(new ChatMessage("{\"text\":\"White Phosphorus\"}"));
    }

    private void setOnFire(){
        this.setOnFire(Integer.MAX_VALUE);
        this.setInvisible(true);
    }

    protected void a(MovingObjectPositionEntity position) {
        Entity hit = position.getEntity();
        int damage = hit instanceof EntityBlaze ? 0 : 6;

        Vec3D vector = position.getPos();
        Block block = world.getBlockAt((int)vector.getX(),(int)vector.getY(),(int)vector.getZ());
        Block below = block.getLocation().clone().add(0,-1,0).getBlock();
        if (block.getType().isAir() && !below.getType().isAir())  {
            block.setType(Material.FIRE);
        }

        hit.damageEntity(DamageSource.projectile(this, this.getShooter()),damage);
        hit.setOnFire(300);
    }

    protected void a(MovingObjectPosition var0) {
      //  super.a(var0);
        if (!this.t.y) {
            Vec3D vector = var0.getPos();
            Block block = world.getBlockAt((int)vector.getX(),(int)vector.getY(),(int)vector.getZ());
            if (block.getType().isFlammable()) {
                Block air = block.getLocation().add(0,1,0).getBlock();
                if (air.getType().isAir())
                    air.setType(Material.FIRE);
            }

            this.die();
        }

    }
}
