package me.camm.productions.fortressguns.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileSnowballFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class AbstractFlare extends ProjectileSnowballFG {

    protected Random rand;


    public AbstractFlare(World world, double x, double y, double z, @Nullable EntityPlayer shooter) {
        super(world, x,y,z, shooter);
        rand = new Random();
    }



    ///code to allow projectile-projectile collisions
    @Override
    protected boolean a(net.minecraft.world.entity.Entity entity) {

        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
            net.minecraft.world.entity.Entity entity1 = shooter;
            return entity1 == null || !entity1.isSameVehicle(entity);
            //so either the bullet has no shooter, and they're not stacked and not enderman
            //removed the check isInteractable()
        } else {
            return false;
        }
    }
}
