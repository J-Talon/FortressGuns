package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class CommandDebug extends CommandHandler {

    private static final double DISTANCE = 20;

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) return true;

        Entity e = raytrace(player);
        if (e==null) return true;

        Location loc = e.getLocation().add(0,0,5);
        World world = player.getWorld();


        AllocatorVanillaE allocator = new AllocatorVanillaE(world, loc.toVector());
        List<Tuple2<Float, Entity>> es = allocator.allocate(new Tuple2<>(5f,player));
        for (Tuple2<Float, Entity> ce: es) {
            sender.sendMessage(ce.getA()+" "+ce.getB().getType());
        }

        return true;

    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }


    private @Nullable Entity raytrace(Player player) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();

        World world = player.getWorld();

        Predicate<Entity> pred = new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                return !entity.getUniqueId().equals(player.getUniqueId());
            }
        };

        RayTraceResult res = world.rayTraceEntities(eye, direction, DISTANCE, pred);
        if (res == null) {
            return null;
        }

        return res.getHitEntity();

    }


    @Override
    public String getPermissionNode() {
        return PermissionNodeLabel.FG_DEBUG.label();
    }
}
