package me.camm.productions.fortressguns.Util.command;

import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Serialization.FactorySerialization;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static me.camm.productions.fortressguns.Util.command.PermissionNodeLabel.FG_DEBUG;

public class CommandFGInspect extends CommandHandler {

    private static final double DISTANCE = 10;


    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be in-world to use this command");
            return true;
        }

        if (args.length > 0) return false;

        Entity hit = raytrace(player);
        if (hit == null) {
            sender.sendMessage("No entity found");
            return true;
        }

        infoPDC(player, hit);
        infoConstruct(player, hit);

        if (!(hit instanceof LivingEntity living)) {
            return true;
        }

        living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20,0));
        return true;
    }


    private void infoConstruct(Player player, @NotNull Entity entity) {

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)entity).getHandle();

        if (nmsEntity instanceof Component c) {
            player.sendMessage("Entity is a custom FG component type");
            try {
                Location loc = c.getBody().getCoreEntity().getLocation();
                player.sendMessage("Core location: "+loc);
            }
            catch (NullPointerException e) {
                player.sendMessage("Could not get core location. Component might be invalid");
            }
        } else {
            player.sendMessage("Entity is not a FG component type");
        }

    }


    private void infoPDC(Player player, @NotNull Entity entity) {

        EntityType type = entity.getType();
        UUID id = entity.getUniqueId();
        PersistentDataContainer pdc = entity.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(FortressGuns.getInstance(), FactorySerialization.getKey());
        boolean has = pdc.has(key, PersistentDataType.INTEGER_ARRAY);

        player.sendMessage("Entity type: "+ type);
        player.sendMessage("UUID: "+id);

        int[] vals = pdc.get(key,PersistentDataType.INTEGER_ARRAY);

        if (!has || vals == null) {
            player.sendMessage("PDC: No PDC found");
            return;
        }

        StringBuilder s = new StringBuilder();
        for (int i : vals) {
            s.append(" ").append(i);
        }

        player.sendMessage("PDC: "+s);
    }



    private @Nullable Entity raytrace(Player player) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();

        World world = player.getWorld();

        class TracePredicate implements Predicate<Entity> {

            @Override
            public boolean test(Entity entity) {
                return ! player.getUniqueId().equals(entity.getUniqueId());
            }
        }

        RayTraceResult res = world.rayTraceEntities(eye, direction, DISTANCE, new TracePredicate());
        if (res == null) {
            return null;
        }

        return res.getHitEntity();

    }


    @Override
    public List<String> getTabCompletes(CommandSender sender, String[] in) {
        return List.of();
    }


    @Override
    public String getPermissionNode() {
        return FG_DEBUG.label();
    }
}
