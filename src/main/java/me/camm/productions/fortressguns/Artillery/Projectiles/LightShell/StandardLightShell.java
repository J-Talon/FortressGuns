package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        Vec3D motion = getMot().d().a(-0.1f);
        hitEntity.i(motion.getX(), motion.getY(), motion.getZ());
        return super.onEntityHit(hitEntity, entityPosition);
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        return super.onBlockHit(exactHitPosition, blockFace, hitBlock);
    }


    public static void setHitDamage(float hitDamage) {
        StandardLightShell.hitDamage = hitDamage;
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }
}
