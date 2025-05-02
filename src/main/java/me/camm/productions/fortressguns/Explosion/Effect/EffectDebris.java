package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class EffectDebris extends ExplosionEffect<Double> {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {
        double percent;
        if (context == null)
            percent = 1;
        else percent = context;

        World w = explosion.getWorld();
        Location loc = new Location(explosion.getWorld(), explosion.getX(), explosion.getY(), explosion.getZ());
        w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc, (int)(30 * percent),1,0.5,1,0);
        w.spawnParticle(Particle.BLOCK_CRACK,loc,(int)(500 * percent),1,1,1,1);
        w.spawnParticle(Particle.WHITE_ASH,loc,(int)(100 * percent),0.5,1,0.5,1);
        w.playSound(loc, Sound.BLOCK_WET_GRASS_BREAK,1,0);
        w.playSound(loc, Sound.BLOCK_GRASS_BREAK,1,0);
    }
}
