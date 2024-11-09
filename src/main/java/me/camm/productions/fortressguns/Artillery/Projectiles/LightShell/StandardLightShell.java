package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

public class StandardLightShell extends LightShell {


    public StandardLightShell(World world, double x, double y, double z, EntityHuman human, Artillery source) {
        super(world, x, y, z, human, source);
    }

    @Override
    public void preTerminate(MovingObjectPosition hit) {

        if (hit == null) {
            return;
        }

        Vec3D pos = hit.getPos();

        CraftWorld bukkit = getWorld().getWorld();
        if (hit instanceof MovingObjectPositionBlock) {
            Location position = new Location(bukkit, pos.getX(), pos.getY(), pos.getZ());

            CraftBlock block = (CraftBlock) bukkit.getBlockAt(new Location(bukkit, pos.getX(), pos.getY(), pos.getZ()));
            if (block.getType().isAir())
                return;

            bukkit.spawnParticle(Particle.BLOCK_CRACK, position, 10, 0, 0, 0, block.getBlockData());
         //   float hardness = block.getType().getHardness();
            //do something with sparks here? < decorative
        }


    }

    @Override
    public float getDamageStrength() {
        return 10;
    }
}
