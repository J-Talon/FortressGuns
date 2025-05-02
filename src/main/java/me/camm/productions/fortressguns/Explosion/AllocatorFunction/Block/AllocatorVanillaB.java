package me.camm.productions.fortressguns.Explosion.AllocatorFunction.Block;

import me.camm.productions.fortressguns.Explosion.Abstract.Allocator;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllocatorVanillaB extends Allocator<List<Block>,Float> {

    protected static Random rand = new Random();

    public AllocatorVanillaB(World world, Vector position) {
        super(world, position);
    }

    //input context: the radius of the explosion
    @Override
    public List<Block> allocate(Float inputContext) {

        List<Block> brokenBlocks = new ArrayList<>();

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
                    float power = inputContext * (0.7F + rand.nextFloat() * 0.6F);

                    //direction of an explosion fragment
                    double vectorX = x;
                    double vectorY = y;
                    double vectorZ = z;

                    //air resistance affecting the explosion
                    final double AIR_RESIST = 0.22500001F;

                    while (power > 0) {
                        Block block = world.getBlockAt((int)vectorX, (int)vectorY, (int)vectorZ);
                        float blastResistance = block.getType().getBlastResistance();
                        power -= (blastResistance + 0.3F) * resolution;

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
