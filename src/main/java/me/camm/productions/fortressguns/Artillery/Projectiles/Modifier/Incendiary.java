package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.Artillery.Projectiles.BurningSphere;
import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class Incendiary implements IModifier {

    private final Location loc;
    private static final Queue<BurningSphere> pool;

    private static final int POOL_SIZE = 100;
    private static final Random rand;


    static {
        pool = new LinkedList<>();
        rand = new Random();

    }


    public Incendiary(Location loc) {
        this.loc = loc;
        while (pool.size() < POOL_SIZE && loc.getWorld() != null) {
            pool.add(new BurningSphere(((CraftWorld)loc.getWorld()).getHandle(),0,0,0));
        }

    }

    @Override
    public void activate() {

    World world = loc.getWorld();


    if (world == null)
        return;

    new BukkitRunnable() {
        public void run(){

            for (double vertical=90;vertical>=-90;vertical -= rand.nextDouble()*60)
            {
                double yComponent = Math.tan(vertical); //y value
                for (double horizontal=0;horizontal<=360;horizontal+=rand.nextDouble()*60)
                {
                    double xComponent = Math.sin(horizontal);  //x Value of the vector
                    double zComponent = Math.cos(horizontal);  //z Value of the vector

                    BurningSphere sphere = pool.poll();
                    if (sphere == null)
                        break;

                    double x, y, z;
                    x = loc.getX();
                    y = loc.getY();
                    z = loc.getZ();

                    sphere.setLocation(x,y,z,0,0);
                    sphere.setPosition(x,y,z);

                    ((CraftWorld) world).addEntity(sphere, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    yComponent *= rand.nextDouble();
                    xComponent *= rand.nextDouble();
                    zComponent += rand.nextDouble();
                    sphere.setMot(xComponent*-0.5,yComponent*1.5,zComponent*-0.5);
                }
            }

            cancel();
        }
    }.runTask(FortressGuns.getInstance());

    }
}
