package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Util.Math.MathFG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class CommandDebug extends CommandHandler {

    private static final double DISTANCE = 10;

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) return true;

        Block block = raytrace(player);
        if (block == null) return true;

        Location loc = block.getLocation().add(0.5,0.6,0.6);
        List<Block> blocks = MathFG.getRoughCollisions(loc,player.getWorld());


        for (Block b: blocks) {
            if (b.getType().isAir())
                b.setType(Material.GLASS);
        }

        return true;

    }

    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }


    private @Nullable Block raytrace(Player player) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();

        World world = player.getWorld();


        RayTraceResult res = world.rayTraceBlocks(eye, direction, DISTANCE);
        if (res == null) {
            return null;
        }

        return res.getHitBlock();
    }


    @Override
    public String getPermissionNode() {
        return PermissionNodeLabel.FG_DEBUG.label();
    }
}
