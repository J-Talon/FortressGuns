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
