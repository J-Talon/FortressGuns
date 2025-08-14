package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public interface ProjectileFG {

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onEntityHit(Entity hitEntity, Vec3D entityPosition);

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock);

     //will add config for this later
     default float getWeight() {
          return 0;
     }

     void remove();

     float getHitDamage();


     default void onWaterEnter() {
     }

     default void onLavaEnter() {
     }



}
