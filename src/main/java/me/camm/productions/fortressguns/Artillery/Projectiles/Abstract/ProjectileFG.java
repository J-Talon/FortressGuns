package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface ProjectileFG {

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onEntityHit(Entity hitEntity, Vector entityPosition);

     //@return: whether the collision terminated flight
     //called after preOnHit
     boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock);

     //will add config for this later
     default float getWeight() {
          return 0;
     }

     void remove();

     float getHitDamage();


     void setVelocity(double x, double y, double z);


     void addToWorld(World world);


     default void onWaterEnter() {
     }

     default void onLavaEnter() {
     }



}
