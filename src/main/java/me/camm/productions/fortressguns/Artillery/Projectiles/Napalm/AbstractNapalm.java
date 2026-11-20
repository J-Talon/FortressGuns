package me.camm.productions.fortressguns.Artillery.Projectiles.Napalm;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNapalm extends ProjectileArrowFG {
    public AbstractNapalm(org.bukkit.World world, double x, double y, double z, @Nullable Player shooter, Artillery source) {
        super(world, x, y, z, shooter);
    }

    public abstract boolean onEntityHit(Entity hitEntity, Vector entityPosition);

    public abstract boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock);
}
