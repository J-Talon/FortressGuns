package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.interact.IBHandle;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class IBDevSpyglass implements InteractionBehaviourItem {

    private final Map<UUID, Entity> targets = new HashMap<>();
    private static final Material itemMat = Material.SPYGLASS;


    @Override
    public boolean accept(Tuple2<Player, ItemStack> tup) {
        ItemStack stack = tup.getB();
        return stack != null && stack.getType() == itemMat;
    }

    @Override
    public Material[] getLabels() {
        return new Material[]{itemMat};
    }


    @Override
    public @Nullable IBHandle getHandle() {
        return IBHandle.DEV_SPYGLASS_TARGET;
    }

    @Override
    public void onLCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        updateTarget(player.getUniqueId(), player);

//        Predicate<Entity> entityPredicate = new Predicate<org.bukkit.entity.Entity>() {
//            @Override
//            public boolean test(org.bukkit.entity.Entity entity) {
//                return !(entity.equals(player));
//            }
//        };
//
//        RayTraceResult res = world.rayTraceEntities(player.getEyeLocation(),player.getEyeLocation().getDirection(),200, 1, entityPredicate);
//        if (res == null)
//            return;
//
//        org.bukkit.entity.Entity hit = res.getHitEntity();
//        if (hit == null)
//            return;
//
//        updateTarget(player.getUniqueId(), hit);

        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER,1,1);
        player.sendMessage("target");
     //   player.sendMessage(ChatColor.RED+"[Development only] Target Acquired: "+hit.getType());
    }

    public void updateTarget(UUID id, org.bukkit.entity.Entity target) {
        targets.put(id, target);
    }

    public org.bukkit.entity.Entity getTarget(UUID id) {
        return targets.getOrDefault(id, null);
    }
}
