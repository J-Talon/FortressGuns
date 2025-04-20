package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AllocatorVanillaB extends Allocator<List<BlockPosition>,Float> {


    public AllocatorVanillaB(World nmsWorld, Vec3D position) {
        super(nmsWorld, position);
    }

    //input context: the radius of the explosion
    @Override
    public List<BlockPosition> allocate(Float inputContext) {

        List<BlockPosition> brokenBlocks = new ArrayList<>();

        int wallX;
        int wallY;

        double x,y,z;
        x = position.getX();
        y = position.getY();
        z = position.getZ();

        float radMin = inputContext - 1;

        //create a hollow cube
        for(int wallZ = 0; wallZ < inputContext; ++wallZ) {
            for(wallX = 0; wallX < inputContext; ++wallX) {
                for(wallY = 0; wallY < inputContext; ++wallY) {

                    if (!(wallZ == 0 || wallZ == radMin || wallX == 0 || wallX == radMin || wallY == 0 || wallY == radMin)) {
                        continue;
                    }

                    //x,y,z coordinates
                    //this initially makes a hollow cube
                    //n / radMin * 2 - 1 gives the coordinates of the wall

                    //shifting the cube to the origin
                    double bumpX = ((float)wallZ / radMin * 2.0F - 1.0F);
                    double bumpY = ((float)wallX / radMin * 2.0F - 1.0F);
                    double bumpZ = ((float)wallY / radMin * 2.0F - 1.0F);

                    double length = Math.sqrt(bumpX * bumpX + bumpY * bumpY + bumpZ * bumpZ);

                    //performing a transformation
                    //which transforms the cube into a sphere by normalization (think vectors)
                    bumpX /= length;
                    bumpY /= length;
                    bumpZ /= length;

                    //w = random
                    float power = inputContext * (0.7F + nmsWorld.w.nextFloat() * 0.6F);

                    //direction of an explosion fragment
                    double vectorX = x;
                    double vectorY = y;
                    double vectorZ = z;

                    //air resistance affecting the explosion
                    final double AIR_RESIST = 0.22500001F;

                    while (power > 0) {
                        BlockPosition block = new BlockPosition(vectorX, vectorY, vectorZ);

                        //is in world bounds
                        if (!nmsWorld.isValidLocation(block)) {
                            break;
                        }

                        IBlockData iblockdata = nmsWorld.getType(block);
                        Fluid fluid = nmsWorld.getFluid(block);

                        //calculator.a = getBlockExplosionResistance
                        Optional<Float> blockResistance = getBlockResistance(iblockdata, fluid);

                        if (blockResistance.isPresent()) {
                            ///this expansion factor is probably from
                            //the game devs testing what value is the best for expansion without
                            //accidently missing a block or checking a block more than once
                            power -= (blockResistance.get() + 0.3F) * resolution;
                        }

                        //a = shouldBlockExplode
                        if (power > 0.0F) {
                            brokenBlocks.add(block);
                        }

                        //expanding the vectors of the bump
                        vectorX += bumpX * resolution;
                        vectorY += bumpY * resolution;
                        vectorZ += bumpZ * resolution;
                        power -= AIR_RESIST;
                    }
                }
            }
        }

        return brokenBlocks;
    }
}
