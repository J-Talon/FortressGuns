package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class StandardShell extends Shell {


    private final double startingY;



    public StandardShell(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter) {
        super(entitytypes, x, y, z, world, shooter);
        startingY = y;

    }

    public void tick() {
        super.tick();

        if (getMot().getY() >= 0)
            return;

        double diff = Math.abs(locY() - startingY);


        SoundPlayer whistle = new SoundPlayer() {
            @Override
            public void playSound(Location loc) {

                float pitch = (1/30f) * (float)diff;

                float volume = (-1/30f) * (float)diff + 2;

                bukkitWorld.playSound(loc,Sound.ENTITY_GUARDIAN_AMBIENT_LAND,Math.min(volume,2),Math.max(2,pitch));
            }
        };

        playSound(whistle,5);
    }

    @Override
    protected void explode(MovingObjectPosition pos) {
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
