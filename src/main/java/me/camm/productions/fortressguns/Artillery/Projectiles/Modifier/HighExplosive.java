package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tracer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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
        for (double vertical=90;vertical>=-90;vertical-=20)
        {
            double yComponent = Math.tan(vertical); //y value
            for (double horizontal=0;horizontal<=360;horizontal+=20)
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
                }

                for (Block block: total) {

                    if (block == null || block.getType().isAir())
                        continue;

                        BlockData data = block.getBlockData();

                        FallingBlock thrown = world.spawnFallingBlock(block.getLocation(),data);
                        block.setType(Material.AIR);
                        thrown.setDropItem(false);
                        thrown.setHurtEntities(true);

                        thrown.setVelocity(tracers.get(rand.nextInt(tracers.size())).getDirection()
                                .add(new Vector(0,0.5,0)).multiply(rand.nextDouble()));

                }



                cancel();

            }
        }.runTask(FortressGuns.getInstance());




    }
}
