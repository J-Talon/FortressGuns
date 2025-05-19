package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;

import org.jetbrains.annotations.Nullable;

public abstract class ProjectileSnowballFG extends EntitySnowball implements ArtilleryProjectile {


    protected EntityPlayer shooter;
    protected boolean enteredWater;
    protected boolean enteredLava;

    public ProjectileSnowballFG(@Nullable World world, double x, double y, double z, @Nullable EntityPlayer human) {
        super(world,x,y,z);
        this.shooter = human;
        enteredLava = false;
        enteredWater = false;
    }

    public org.bukkit.World bukkitWorld() {
        return getWorld().getWorld();
    }

    @Override
    public void a(MovingObjectPositionEntity entityHit) {
        onEntityHit(entityHit.getEntity(), entityHit.getPos());
    }

    @Override
    public void a(MovingObjectPositionBlock pos) {
        onBlockHit(pos.getPos(), pos.getDirection(), pos.getBlockPosition());
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


}
