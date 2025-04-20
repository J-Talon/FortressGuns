package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileArrowFG extends EntityArrow implements ArtilleryProjectile {

    protected EntityPlayer shooter;
    protected org.bukkit.World bukkitWorld;



    protected static final ItemStack item;
    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);
    }


    @Override
    protected ItemStack getItemStack() {
        return item;
    }

    public ProjectileArrowFG(World world, double x, double y, double z, @Nullable EntityPlayer shooter) {
        super(EntityTypes.d, x, y, z, world);

        if (shooter != null) {
            this.shooter = shooter;
            setShooter(this.shooter);
        }

        bukkitWorld = getWorld().getWorld();
    }


    @Override
    public void inactiveTick() {
        double motion = getMot().g(); //length Squared
        if (motion < 10E-3 || this.b) {
            remove();
        }
    }

    @Override
    public void remove() {
        this.die();
    }

    @Override
    public void a(MovingObjectPositionEntity pos) {
        onEntityHit(pos.getEntity(),pos.getPos());
    }

    @Override
    public void a(MovingObjectPositionBlock pos) {
        BlockPosition blockPos = pos.getBlockPosition();
        onBlockHit(pos.getPos(), pos.getDirection(), blockPos);
    }





}
