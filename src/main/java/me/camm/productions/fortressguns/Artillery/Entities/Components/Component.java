package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Component {

   public void remove();

   public @NotNull Construct getBody();

   //returns the first rider in the list of passengers
   public @Nullable Entity getRider();

   public void startRiding(Player player);

   public void teleport(double x, double y, double z);

   public void teleport(@NotNull Location loc);

   public void setRotation(@NotNull EulerAngle angle);

   public void setRotation(float x, float y);

   public boolean spawn();



       /*
      spigot doesn't treat armor stand interactions the same way as other entities
      so no, we won't be using behaviours here, at least not purely
     */

   public void onRightClick(ItemStack mainHand, Player clicked);


   //return true: whether the entity was damaged
    //return false: whether the entity took no damage
   public boolean onLeftClick(ItemStack mainHand, Player clicked);

   //enough space in the next area
   public boolean enoughSpace(Vector nextPosition);


   public boolean enoughSpace();

   public UUID getUUID();

   public PersistentDataContainer getPDC();

   public Location getLocation();

   public boolean isValid();

   public Entity getEntity();

   public World getBukkitWorld();


   /*          ---------/ |
                       /  |  H
            ----------/   |
                      |   /
                      |  /  L
                      | /
               --------/
                 W


              base
               []     <-- L -->
             [][][][]==========  ==>
               []                 ^-- bullet

   Given an artillery barrel,  the length is the dimension going away from the base

    */


    /*
    for armorstands, the positional height, width, and length are the dimensions of the head block render
    NOT the hitbox dimensions
     */
   public double getPositionalHeight();

   public double getPositionalWidth();

   public double getPositionalLength();


   /*
   for armorstands this is the eye location
    */
   public Location getCenterPoint();




}
