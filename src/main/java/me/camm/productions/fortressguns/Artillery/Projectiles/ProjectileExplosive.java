package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.phys.Vec3D;
import org.bukkit.GameRule;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.Nullable;

public interface ProjectileExplosive {

    public void explode(@Nullable Vec3D hit);

    default void postExplosion(EntityExplodeEvent event) {
        org.bukkit.World world = event.getEntity().getWorld();

        boolean rule = Boolean.TRUE.equals(world.getGameRuleValue(GameRule.MOB_GRIEFING));
        //change this to config later
        if (!rule) {
            event.blockList().clear();
        }
    }


}
