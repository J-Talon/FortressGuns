package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public class CRAMShell extends LightShell implements ProjectileExplosive {

    private static float hitDamage = 10;



    public CRAMShell(@Nullable World world, double x, double y, double z, @Nullable EntityHuman human, Artillery source) {
        super(world, x, y, z, human, source);
    }

    public static void setHitDamage(float hitDamage) {
        CRAMShell.hitDamage = hitDamage;
    }

    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    @Override
    public void explode(@Nullable Vec3D hit) {

    }


    public void tick() {


        //this is to do with nbt and game events
        //not sure if this is important

//        if (!this.e) {
//            this.a(GameEvent.J, this.getShooter(), this.getChunkCoordinates());
//            this.e = true;
//        }
//
//        if (!this.d) {
//            this.d = this.i();
//        }

        entityBaseTick();

        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this::a);
        boolean flag = false;
        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.b) {
            BlockPosition blockposition = ((MovingObjectPositionBlock)movingobjectposition).getBlockPosition();
            IBlockData iblockdata = this.t.getType(blockposition);
            if (iblockdata.a(Blocks.db)) {
                this.d(blockposition);
                flag = true;
            } else if (iblockdata.a(Blocks.iT)) {
                TileEntity tileentity = this.t.getTileEntity(blockposition);
                if (tileentity instanceof TileEntityEndGateway && TileEntityEndGateway.a(this)) {
                    TileEntityEndGateway.a(this.t, blockposition, iblockdata, this, (TileEntityEndGateway)tileentity);
                }

                flag = true;
            }
        }

        if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.a && !flag) {
            this.preOnHit(movingobjectposition);
        }

        this.checkBlockCollisions();
        Vec3D vec3d = this.getMot();
        double d0 = this.locX() + vec3d.b;
        double d1 = this.locY() + vec3d.c;
        double d2 = this.locZ() + vec3d.d;
        this.z();
        float drag;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                this.t.addParticle(Particles.f, d0 - vec3d.b * 0.25, d1 - vec3d.c * 0.25, d2 - vec3d.d * 0.25, vec3d.b, vec3d.c, vec3d.d);
            }

            drag = 0.8F;
        } else {
            drag = 1F;
        }

        this.setMot(vec3d.a((double)drag));
        if (!this.isNoGravity()) {
            Vec3D vec3d1 = this.getMot();
            this.setMot(vec3d1.b, vec3d1.c - (double)this.l(), vec3d1.d);
        }

        this.setPosition(d0, d1, d2);
    }


    @Override
    public float getExplosionPower() {
        return 1;
    }

    @Override
    public void preHit(@Nullable MovingObjectPosition hitPosition) {

    }


}
