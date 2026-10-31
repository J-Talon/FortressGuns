package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import me.camm.productions.fortressguns.Util.Math.MathFG;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileArrowFG extends EntityArrow implements ProjectileFG {

    protected EntityPlayer shooter;
    protected org.bukkit.World bukkitWorld;

    protected boolean enteredWater = false;
    protected boolean enteredLava = false;


    public ItemStack getItemStack() {
        return Items.mh.createItemStack();
    }

    public ProjectileArrowFG(World world, double x, double y, double z, @Nullable Player shooter) {
        super(EntityTypes.d, x, y, z, ((CraftWorld)world).getHandle());

        if (shooter != null) {
            this.shooter = ((CraftPlayer) shooter).getHandle();
            setShooter(this.shooter);
        }

        bukkitWorld = t.getWorld();
    }


    @Override
    public void inactiveTick() {
        if (this.b || this.au > 20)  // in ground, in ground ticks
            remove();
    }

    @Override
    public void tick() {
        if (isInWater() && !enteredWater) {
            enteredWater = true;
            onWaterEnter();
        }
        else if (!isInWater()) {
            enteredWater = false;
        }

        if (this.aX() && !enteredLava) {
            enteredLava = true;
            onLavaEnter();
        }
        else if (!this.aX()) {
            enteredLava = false;
        }
        super.tick();
    }

    @Override
    public void remove() {
        this.die();
    }

    @Override
    public void a(MovingObjectPositionEntity pos) {
        Entity entityNMS = pos.getEntity();
        onEntityHit(entityNMS.getBukkitEntity(), CraftVector.toBukkit(pos.getPos()));
    }

    @Override
    public void a(MovingObjectPositionBlock pos) {
        BlockPosition blockPos = pos.getBlockPosition();
        org.bukkit.World bukkitWorld = t.getWorld();
        EnumDirection direction = pos.getDirection();
        BaseBlockPosition bbp = direction.p();

        onBlockHit(CraftVector.toBukkit(pos.getPos()),
                MathFG.toCardinalDirection(bbp.getX(), bbp.getY(), bbp.getZ()),
                bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }


    @Override
    public void setVelocity(double x, double y, double z) {
        this.setMot(x,y,z);
    }

    @Override
    public void addToWorld(org.bukkit.World world) {
        ((CraftWorld)world).getHandle().addEntity(this);
    }

}
