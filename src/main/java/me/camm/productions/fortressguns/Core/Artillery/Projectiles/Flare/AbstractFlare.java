package me.camm.productions.fortressguns.Core.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Core.Artillery.Projectiles.Abstract.ProjectileSnowballFG;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class AbstractFlare extends ProjectileSnowballFG {

    protected Random rand;


    public AbstractFlare(World world, double x, double y, double z, @Nullable Player shooter) {
        super(world, x,y,z, shooter);
        rand = new Random();
    }


    @Override
    public void setVelocity(double x, double y, double z) {
        this.setMot(x,y,z);
    }


    @Override
    public void addToWorld(org.bukkit.World world) {
        ((CraftWorld)world).getHandle().addEntity(this);
    }


    //you probably don't need this
//
//    ///code to allow projectile-projectile collisions
//    @Override
//    protected boolean a(net.minecraft.world.entity.Entity entity) {
//
//        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
//            net.minecraft.world.entity.Entity entity1 = shooter;
//            return entity1 == null || !entity1.isSameVehicle(entity);
//            //so either the bullet has no shooter, and they're not stacked and not enderman
//            //removed the check isInteractable()
//        } else {
//            return false;
//        }
//    }
}
