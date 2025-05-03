package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class EffectDebris extends ExplosionEffect<Tuple2<Double,Block>> {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Tuple2<Double, Block> context) {
        World w = explosion.getWorld();
        Location loc = new Location(explosion.getWorld(), explosion.getX(), explosion.getY(), explosion.getZ());

        double percent;
        BlockData data;
        if (context == null) {
            percent = 1;
            data = loc.getBlock().getBlockData();
        }
        else {
            percent = context.getA();
            Block contextBlock = context.getB();
            if (contextBlock == null) {
                data = Material.AIR.createBlockData();
            }
            else data = contextBlock.getBlockData();
        }



        w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc, (int)(30 * percent),1,0.5,1,0);
        w.spawnParticle(Particle.BLOCK_CRACK,loc,(int)(500 * percent),1,1,1,1,data);
        w.spawnParticle(Particle.WHITE_ASH,loc,(int)(100 * percent),0.5,1,0.5,1);
        w.playSound(loc, Sound.BLOCK_WET_GRASS_BREAK,1,0);
        w.playSound(loc, Sound.BLOCK_GRASS_BREAK,1,0);
    }
}
