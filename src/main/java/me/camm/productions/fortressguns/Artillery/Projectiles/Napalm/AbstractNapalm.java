package me.camm.productions.fortressguns.Artillery.Projectiles.Napalm;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNapalm extends ProjectileArrowFG {
    public AbstractNapalm(World world, double x, double y, double z, @Nullable EntityPlayer shooter) {
        super(world, x, y, z, shooter);
    }
}
