package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /*

    I think the way forward is this:

    Interface Component
        v             v
 ComponentAS1.17.1    ComponentDE1.17.1 ....
 v                    v
 ASCore1.17.1         DECore1.17.1
 ASPart1.17.1         DECore.1.17.1

 ...

 ASCore implements ICore // OR:
 getComponentType() <== which I think is probably more performant than going down the tree

 Interactions will be handled via behaviours



     */

}
