package me.camm.productions.fortressguns.Artillery.Projectiles;



import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;


import javax.annotation.Nullable;

public class FlakShell extends Shell {

    int flightTime;
    double explodeTime;

    private Entity terminus = null;

    public FlakShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world,shooter);
        flightTime = 0;
        explodeTime = 1;
    }

    public float getStrength() {
        return 3f;
    }

    public void setTerminus(Entity target){
        this.terminus = target;

        if (terminus != null) {

            double speed = this.getMot().f();
            double delX = terminus.locX() - locX();
            double delY = terminus.locY() - locY();
            double delZ = terminus.locZ() - locZ();

            double distance = Math.sqrt(delX * delX + delY * delY + delZ * delZ);
            explodeTime = (int)(distance / speed);
        }

    }



    @Override
    public void tick(){
        super.tick();

        if (terminus == null || terminus.isRemoved() || !terminus.isAlive()) {
            return;
        }

        if (flightTime > explodeTime) {
                    explode();
                    return;
                }

        flightTime ++;
    }


    private void explode(){
        this.die();

        getWorld().createExplosion(this ,u,v,w,4, false, Explosion.Effect.c);
        playExplosionEffects(new Location(bukkitWorld,u,v,w));

    }

     public void playExplosionEffects(Location explosion){
        bukkitWorld.spawnParticle(Particle.SMOKE_LARGE,explosion,50,0.1,0.1,0.1,0.2f);
        bukkitWorld.spawnParticle(Particle.SQUID_INK,explosion,50,0.1,0.1,0.1,0.2f);


        //explode and make a sound
        bukkitWorld.playSound(explosion, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,1,2);
        bukkitWorld.playSound(explosion, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,1,0.2f);

    }
}
