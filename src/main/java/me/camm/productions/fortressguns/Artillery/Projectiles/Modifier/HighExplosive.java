package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tracer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.Math.PI;

public class HighExplosive implements IModifier {

    private final Location loc;
    public HighExplosive(Location target){
        loc = target;
    }

    @Override
    public void activate() {


        World world = loc.getWorld();
        Random rand = new Random();

        if (world == null)
            return;

        ArrayList<Tracer> tracers = new ArrayList<>();

        //quarter = 90 deg
        final double QUARTER = PI/2;

        //20 deg incrementation
        final double INCREMENT = 20*PI/180;

        //360 deg
        final double TWO_RADS = PI*2;

        for (double vertical=QUARTER;vertical>=-(QUARTER);vertical-=INCREMENT)
        {
            double yComponent = Math.tan(vertical); //y value
            for (double horizontal=0;horizontal<=TWO_RADS;horizontal+=INCREMENT)
            {
                double xComponent = Math.sin(horizontal);  //x Value of the vector
                double zComponent = Math.cos(horizontal);  //z Value of the vector

                Tracer tracer = new Tracer(new Vector(xComponent,yComponent,zComponent), loc.clone().toVector(),world);

                tracers.add(tracer);
            }
        }

        tracers.add( new Tracer(new Vector(0,1,0), loc.clone().toVector(),world));
        tracers.add( new Tracer(new Vector(0,-1,0), loc.clone().toVector(),world));


        new BukkitRunnable() {
            @Override
            public void run() {


                HashSet<Block> total = new HashSet<>();
                for (Tracer tracer: tracers){
                   HashSet<Block> broken = tracer.breakBlocks();
                   total.addAll(broken);
                   if (broken.size() > 10)
                       break;
                }
                playExplosionEffects(world,loc);


                int thrown = 0;
                net.minecraft.world.level.World nmsWorld = ((CraftWorld)world).getHandle();
                List<Entity> blocks = new ArrayList<>();
                for (Block block: total) {

                    if (block == null || block.getType().isAir())
                        continue;

                    BlockData data = block.getBlockData();
                    EntityFallingBlock fallingBlock = new EntityFallingBlock(nmsWorld,
                            loc.getX(), loc.getY(),loc.getZ(),((CraftBlockData)data).getState());

                    thrown ++;
                    Vector velocity = tracers.get(rand.nextInt(tracers.size())).getDirection();
                    fallingBlock.setMot(new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ()));
                    blocks.add(fallingBlock);

                    if (thrown > 10)
                        break;

                }

                blocks.forEach((nmsWorld::addEntity));






                cancel();

            }
        }.runTask(FortressGuns.getInstance());




    }
}
