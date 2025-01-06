package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;


public class StandardLightShell extends LightShell {

    private static float hitDamage = 5;


    public StandardLightShell(World world, double x, double y, double z, EntityPlayer human, Artillery source) {
        super(world, x, y, z, human, source);
    }

    @Override
    public void preHit(MovingObjectPosition hit) {

        if (hit == null) {
            return;
        }

        Vec3D motion = this.getMot();


        if (hit instanceof MovingObjectPositionEntity) {
            Entity hitEntity = ((MovingObjectPositionEntity) hit).getEntity();
            motion = motion.d().a(-0.1f);  //mult -0.1 and normalize

            hitEntity.setMot(motion);
            hitEntity.C = true;
        }
        this.die();
    }


    public static void setHitDamage(float hitDamage) {
        StandardLightShell.hitDamage = hitDamage;
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }
}
