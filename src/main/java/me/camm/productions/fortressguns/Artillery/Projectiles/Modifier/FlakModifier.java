package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.FortressGuns;

import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.Explosion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

/*
flak modifier for flak shots
 */
public class FlakModifier implements IModifier
{


    //location of explosion
    private final Location explosion;
    private final EntityHuman shooter;

    public FlakModifier(Location explosion, @Nullable EntityHuman shooter) {
        this.explosion = explosion;
        this.shooter = shooter;
    }

    //activating the shot
    @Override
    public void activate() {

        World bukkitWorld = explosion.getWorld();
        if (bukkitWorld==null)
            return;

        playFlakEffects(bukkitWorld, explosion);
        Explosion flakExplosion = new Explosion(((CraftWorld)bukkitWorld).getHandle(), shooter,explosion.getX(), explosion.getY(), explosion.getZ(), 3);
        flakExplosion.a();


    }
}
