package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.Artillery.Projectiles.BurningSphere;
import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class Incendiary implements IModifier {

    private final Location loc;

    public Incendiary(Location loc) {
        this.loc = loc;
    }

    @Override
    public void activate() {

    World world = loc.getWorld();
    Random rand = new Random();

    if (world == null)
        return;

    new BukkitRunnable() {
        public void run(){

            for (double vertical=90;vertical>=-90;vertical-= rand.nextDouble()*60)
            {
                double yComponent = Math.tan(vertical); //y value
                for (double horizontal=0;horizontal<=360;horizontal+=rand.nextDouble()*60)
                {
                    double xComponent = Math.sin(horizontal);  //x Value of the vector
                    double zComponent = Math.cos(horizontal);  //z Value of the vector

                    BurningSphere sphere = new BurningSphere(((CraftWorld)world).getHandle(),loc.getX(),loc.getY(),loc.getZ());
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
