package me.camm.productions.fortressguns.Artillery.Projectiles;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SimpleMissile extends EntityArrow {

    private final Player shooter;
    public SimpleMissile(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter) {
        super(entitytypes, x, y, z, world);
        this.shooter = shooter;
    }

    @Override
    protected ItemStack getItemStack() {
        return null;
    }
}
