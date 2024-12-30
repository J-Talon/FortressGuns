package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;

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


    private static float hitDamage = 10;
    private static float explosionPower = 4;


    public StandardHeavyShell(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter) {
        super(entitytypes, x, y, z, world, shooter);
    }


    public static void setHitDamage(float hitDamage) {
        StandardHeavyShell.hitDamage = hitDamage;
    }

    public static void setExplosionPower(float explosionPower) {
        StandardHeavyShell.explosionPower = explosionPower;
    }

    @Override
    public  float getExplosionPower() {
        return explosionPower;
    }

    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void preHit(MovingObjectPosition pos) {
        super.preHit(pos);
        SoundPlayer kaboom = new SoundPlayer() {
            @Override
            public void playSound(Location loc) {
                bukkitWorld.playSound(loc,Sound.ENTITY_LIGHTNING_BOLT_THUNDER,2,0);
            }
        };

        playSound(kaboom,30);
    }
}
