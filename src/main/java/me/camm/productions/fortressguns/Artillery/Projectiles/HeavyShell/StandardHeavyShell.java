package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;

import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShell;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class StandardHeavyShell extends HeavyShell {


    private final double startingY;



    public StandardHeavyShell(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter) {
        super(entitytypes, x, y, z, world, shooter);
        startingY = y;

    }


    public float getDamageStrength() {
        return 3f;
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void preTerminate(MovingObjectPosition pos) {
        super.preTerminate(pos);
        SoundPlayer kaboom = new SoundPlayer() {
            @Override
            public void playSound(Location loc) {
                bukkitWorld.playSound(loc,Sound.ENTITY_LIGHTNING_BOLT_THUNDER,2,0);
            }
        };

        playSound(kaboom,30);
    }

    @Override
    public void playExplosionEffects(Location explosion){
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,explosion,100,0,0,0,0.5);
        bukkitWorld.spawnParticle(Particle.CLOUD,explosion,100,0,0,0,0.5);
    }



}
