package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import me.camm.productions.fortressguns.Util.Math.MathFG;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;

import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectileSnowballFG extends EntitySnowball implements ProjectileFG {


    protected EntityPlayer shooter;
    protected boolean enteredWater;
    protected boolean enteredLava;
    protected org.bukkit.World bukkitWorld;

    public ProjectileSnowballFG(@Nullable World world, double x, double y, double z, @Nullable EntityPlayer human) {
        super(world,x,y,z);
        this.shooter = human;
        enteredLava = false;
        enteredWater = false;
        this.bukkitWorld = t.getWorld();
    }


    @Override
    public void a(MovingObjectPositionEntity entityHit) {
        onEntityHit(entityHit.getEntity().getBukkitEntity(), CraftVector.toBukkit(entityHit.getPos()));
    }

    @Override
    public void a(MovingObjectPositionBlock pos) {
        BaseBlockPosition bbp = pos.getDirection().p();
        BlockPosition blockPos = pos.getBlockPosition();

        onBlockHit(CraftVector.toBukkit(pos.getPos()),
                MathFG.toCardinalDirection(bbp.getX(), bbp.getY(), bbp.getZ()),
                bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    @Override
    public void a(MovingObjectPosition pos) {
        if (pos instanceof MovingObjectPositionBlock block) {
            this.a(block);
            return;
        }

        if (pos instanceof MovingObjectPositionEntity entity) {
            this.a(entity);
        }
    }

    @Override
    public void tick(){
        super.tick();
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
    }


    @Override
    public void remove() {
        this.die();
    }

    //This was code in the superclass which basically showed snowball particles
    //we don't want snowball particles so this goes blank
    @Override
    public void a(byte var0) {}


    @Override
    public void setVelocity(double x, double y, double z) {
        this.setMot(x,y,z);
    }

    @Override
    public void addToWorld(org.bukkit.World world) {
        ((CraftWorld)world).getHandle().addEntity(this);
    }



}
