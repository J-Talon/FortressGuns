package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tracer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.Math.PI;

public class ExplosiveShell extends StandardShell {
    public ExplosiveShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world, shooter);
    }

    @Override
    protected void explode(MovingObjectPosition pos) {

        super.explode(pos);

        Vec3D vec = pos.getPos();
        CraftWorld world = getWorld().getWorld();
        Location loc = new Location(world, vec.getX(), vec.getY(), vec.getZ());

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
                net.minecraft.world.level.World nmsWorld = world.getHandle();
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

    private void playExplosionEffects(org.bukkit.World bukkitWorld, Location explosion){
        bukkitWorld.spawnParticle(Particle.FLASH,explosion,3,0,0,0,0);
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,explosion,30,0,0,0,0.3);
        bukkitWorld.createExplosion(explosion, 4f);
    }


}
