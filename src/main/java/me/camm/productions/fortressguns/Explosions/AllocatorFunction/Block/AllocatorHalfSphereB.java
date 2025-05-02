package me.camm.productions.fortressguns.Explosions.AllocatorFunction.Block;

import me.camm.productions.fortressguns.Explosions.Abstract.Allocator;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllocatorHalfSphereB extends Allocator<Collection<Block>, Tuple2<Float, Vector>> {

    public AllocatorHalfSphereB(World nmsWorld, Vector position) {
        super(nmsWorld, position, 0.5);
    }

    /*

    Input context: Vector directional input


     */
    @Override
    public Collection<Block> allocate(Tuple2<Float, Vector> inputContext) {
        return getTraces(inputContext.getB(), inputContext.getA());
    }


    private Collection<Block> getTraces(Vector in, float radius) {

        List<Block> dots = new ArrayList<>();
        for (Vector vector: getPositions(in,radius)) {

            double vectorX = vector.getX() + position.getX();// - direction.getX();
            double vectorY = vector.getY() + position.getY();//- direction.getY();
            double vectorZ = vector.getZ() + position.getZ();// - direction.getZ();
            double travelled = 0;
            while (travelled < radius) {

                vectorX += (vector.getX() * resolution);
                vectorY += (vector.getY() * resolution);
                vectorZ += (vector.getZ() * resolution);

                travelled += resolution;

                dots.add(world.getBlockAt((int)vectorX, (int)vectorY,(int)vectorZ));
                world.spawnParticle(Particle.END_ROD, new Location(world,vectorX, vectorY, vectorZ),1,0,0,0,0);
            }
        }
        return dots;

    }


    private Collection<Vector> getPositions(Vector in, float radius) {

        Vector direction = in.clone().normalize();

        double wallX;
        double wallY;
        double wallZ;
        float radMin = radius - 1;

        //normalized vector direction
        Vector vec = new Vector(1,0,0);
        Vector rotationAxis = vec.clone().crossProduct(direction).normalize();

        double maxLen = 1;
        double angle = vec.angle(direction);


        int radh = (int)(radius*0.5);


        List<Vector> vecs = new ArrayList<>();

        ////////////////////////////////////////////////////////////
        //0,
        for (wallX = -radh; wallX < 0; wallX+=(resolution*0.5)) {
            for (wallY = 0; wallY < radius ; wallY+= resolution) {
                for (wallZ = 0; wallZ < radius; wallZ+= resolution) {

                    if (!(wallZ == 0 || wallZ == radMin|| wallY == 0 || wallY == radMin  || wallX == -radh /*|| wallX == -1*/)) {
                        continue;
                    }

                    double bumpX = (wallX / radMin * 2) + 2/3f;
                    double bumpY = (wallY / radMin * 2) - 1;
                    double bumpZ = (wallZ / radMin * 2) - 1;


                    double length = Math.sqrt((bumpX) * (bumpX) +
                            (bumpY) * (bumpY)+
                            (bumpZ) * (bumpZ));

                    //adjustment factor
                    //this makes the traces start closer to origin
                    length *= 2;

                    bumpX /= length;
                    bumpY /= length;
                    bumpZ /= length;


                    vecs.add(new Vector(bumpX, bumpY, bumpZ));

                    maxLen = Math.max(maxLen,Math.max(Math.max(bumpX, bumpY), bumpZ));

                }
            }
        }

        for (Vector v: vecs) {
            v.multiply(-1/maxLen);
            v.rotateAroundNonUnitAxis(rotationAxis,angle);
        }
        return vecs;
    }


}
