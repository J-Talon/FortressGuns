package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Util.ExplosionEffect;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.Nullable;

public class StandardShell extends Shell {


    private final double startingY;



    public StandardShell(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter) {
        super(entitytypes, x, y, z, world, shooter);
        startingY = y;

    }


    public float getStrength() {
        return 3f;
    }

    public void tick() {
        super.tick();
       // bukkitWorld.getChunkAt(1,1).getEntities();
//        if (getMot().getY() >= 0)
//            return;

       // double diff = Math.abs(locY() - startingY);


//        SoundPlayer whistle = new SoundPlayer() {
//            @Override
//            public void playSound(Location loc) {
//
//                float pitch = (1/30f) * (float)diff;
//                float volumeX = (-1/30f) * (float)diff + 2;
//
//                float volume = (float)Math.max(1,Math.min(0,(-1/5f) * getMot().getY()));
//                volume *= volumeX;
//
//                if (volume == 0)
//                    return;
//
//                bukkitWorld.playSound(loc,Sound.ENTITY_GUARDIAN_AMBIENT_LAND,Math.min(volume,2),Math.max(2,pitch));
//            }
//        };


        //fix this first and then do it
      //  playSound(whistle,5);
    }


    @Override
    public void explode(MovingObjectPosition pos) {
        super.explode(pos);
        SoundPlayer kaboom = new SoundPlayer() {
            @Override
            public void playSound(Location loc) {
                bukkitWorld.playSound(loc,Sound.ENTITY_LIGHTNING_BOLT_THUNDER,2,0);
            }
        };

        playSound(kaboom,30);
    }



}
