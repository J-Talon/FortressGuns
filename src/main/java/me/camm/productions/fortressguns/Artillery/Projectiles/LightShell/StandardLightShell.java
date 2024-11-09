package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class StandardLightShell extends LightShell {


    public StandardLightShell(World world, double x, double y, double z, EntityHuman human, Artillery source) {
        super(world, x, y, z, human, source);
    }

    @Override
    public void preHit(MovingObjectPosition hit) {

        if (hit == null) {
            return;
        }

        Vec3D motion = this.getMot();


        if (hit instanceof MovingObjectPositionEntity) {
            Entity hitEntity = ((MovingObjectPositionEntity) hit).getEntity();
            motion = motion.d().a(-0.1f);  //mult -0.1 and normalize

            hitEntity.setMot(motion);
            hitEntity.C = true;
        }
    }

    @Override
    public float getDamageStrength() {
        return 10;
    }
}
