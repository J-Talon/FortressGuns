package me.camm.productions.fortressguns.Handlers;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ExplosionHandler implements Listener
{

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){

        float yield = event.getYield();
        org.bukkit.entity.Entity entity = event.getEntity();
        Entity nms = ((CraftEntity)entity).getHandle();

        World world = nms.getWorld();
        double x,y,z;

        Location loc = event.getLocation();
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();

        int x1, x2;

        float yieldTwo = yield*2;

        x1 = MathHelper.floor(x - yieldTwo - 1.0);
        x2 = MathHelper.floor(x + yieldTwo + 1.0);
        int y1 = MathHelper.floor(y - yieldTwo - 1.0);
        int y2 = MathHelper.floor(y + yieldTwo + 1.0);
        int z1 = MathHelper.floor(z - yieldTwo - 1.0);
        int z2 = MathHelper.floor(z + yieldTwo + 1.0);

        List<Entity> list = world.getEntities(nms, new AxisAlignedBB(x1, y1, z1, x2,y2, z2));

        Vec3D vec3d = new Vec3D(x,y,z);

        Explosion dummy = new Explosion(world, nms, x,y,z,yield);
        DamageSource source = DamageSource.explosion(dummy);

        for (Entity e: list) {
            if (e instanceof Component) {

                double damageValue = Math.sqrt(e.e(vec3d)) / (double)yieldTwo;

                double d12 = Explosion.a(vec3d, e);

                double d13 = (1.0 - damageValue) * d12;
                float damage = (float)((int)((d13 * d13 + d13) / 2.0 * 7.0 * (double)yieldTwo + 1.0));
                e.damageEntity(source, damage);
            }
        }

        if (nms instanceof ProjectileExplosive) {
            ((ProjectileExplosive)nms).postExplosion(event);
        }

    }
}
