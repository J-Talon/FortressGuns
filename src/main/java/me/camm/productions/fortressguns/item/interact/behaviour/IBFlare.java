package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IBFlare implements InteractionBehaviourItem {

    private static final String FLARE_NAME = ChatColor.GRAY + "Flares";

    @Override
    public Material[] getLabels() {
        return new Material[] {Material.FIREWORK_ROCKET};
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("RCAir with Flares");
        cancelIfCustomFlare(event);
    }

    @Override
    public void onRCBlock(PlayerInteractEvent event) {
        cancelIfCustomFlare(event);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        cancelIfCustomFlare(event);
    }

    @Override
    public void onDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.FIREWORK_ROCKET)
            return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName())
            return;

        if (meta.getDisplayName().equals(ChatColor.GRAY + "Flares")) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.FIREWORK_ROCKET)
            return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName())
            return;

        if (meta.getDisplayName().equals(ChatColor.GRAY + "Flares")) {
            event.setCancelled(true);
        }
    }

    private void cancelIfCustomFlare(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.FIREWORK_ROCKET)
            return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName())
            return;

        if (meta.getDisplayName().equals(ChatColor.GRAY + "Flares")) {
            event.setCancelled(true);
        }
    }

    private void cancelIfCustomFlare(BlockPlaceEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();;

        if (item == null || item.getType() != Material.FIREWORK_ROCKET)
            return;

        ItemMeta meta = item.getItemMeta();

        if (meta == null || !meta.hasDisplayName())
            return;

        if (meta.getDisplayName().equals(ChatColor.GRAY + "Flares")) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        ItemStack stack = item.getB();

        if (stack == null || stack.getType() != Material.FIREWORK_ROCKET) {
            return false;
        }

        ItemMeta meta = stack.getItemMeta();

        return meta != null
                && meta.hasDisplayName()
                && FLARE_NAME.equals(meta.getDisplayName());
    }
}