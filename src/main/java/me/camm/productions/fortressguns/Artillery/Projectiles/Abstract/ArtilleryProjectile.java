package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

import javax.annotation.Nullable;

public interface ArtilleryProjectile {

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onEntityHit(Entity hitEntity, Vec3D entityPosition);

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock);

     //will add config for this later
     float getWeight();


     void remove();

     float getHitDamage();


     default void onWaterEnter() {
     }

     default void onLavaEnter() {
     }



}
