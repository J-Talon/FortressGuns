package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemCrossbow;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class IBFlare implements InteractionBehaviourItem {

    private static final String FLARE_NAME = ChatColor.GRAY + "Flares";
    private static final NamespacedKey LOADED_FLARE_KEY =
            new NamespacedKey(FortressGuns.getInstance(), "loaded_flare");

    private void fireFlare(Block block) {
        Directional directional =
                (Directional) block.getBlockData();

        BlockFace face = directional.getFacing();

        Location location = block.getLocation()
                .add(0.5, 0.5, 0.5)
                .add(face.getModX() * 0.6,
                        face.getModY() * 0.6,
                        face.getModZ() * 0.6);

        WorldServer world =
                ((CraftWorld) block.getWorld()).getHandle();

        SimpleFlare flare = new SimpleFlare(
                world,
                location.getX(),
                location.getY(),
                location.getZ(),
                face.getModX() * 0.5,
                face.getModY() * 0.5,
                face.getModZ() * 0.5
        );

        world.addEntity(flare);
    }

    @Override
    public Material[] getLabels() {
        return new Material[] {Material.FIREWORK_ROCKET};
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType() == Material.CROSSBOW) {
            ItemStack offhand = player.getInventory().getItemInOffHand();

            if (accept(new Tuple2<>(player, offhand))) {
                event.setCancelled(true);

                player.setFireTicks(100);
            }
        } else {
            cancelIfCustomFlare(event);
        }
    }

    @Override
    public void onRCBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType() == Material.CROSSBOW) {
            ItemStack offhand = player.getInventory().getItemInOffHand();

            if (accept(new Tuple2<>(player, offhand))) {
                event.setCancelled(true);

                player.setFireTicks(100);
            }
        } else {
            cancelIfCustomFlare(event);
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        cancelIfCustomFlare(event);
    }

    @Override
    public void onDispense(BlockDispenseEvent event) {
        if (!accept(new Tuple2<>(null, event.getItem()))) {
            return;
        }

        event.setCancelled(true);

        fireFlare(event.getBlock());

        Dispenser dispenser = (Dispenser) event.getBlock().getState();
        Inventory inventory = dispenser.getInventory();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (!accept(new Tuple2<>(null, stack))) {
                continue;
            }

            stack.setAmount(stack.getAmount() - 1);
            break;
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

    @Override
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack bow = event.getBow();

        // must be a crossbow
        if (bow == null || bow.getType() != Material.CROSSBOW) {
            return;
        }

        // check offhand for custom flares
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (!accept(new Tuple2<>(player, offhand))) {
            return;
        }

        // prevent the normal crossbow projectile from firing
        event.setCancelled(true);

        // consume one flare
        offhand.setAmount(offhand.getAmount() - 1);

        // fire the custom flare
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        WorldServer world =
                ((CraftWorld) player.getWorld()).getHandle();

        SimpleFlare flare = new SimpleFlare(
                world,
                location.getX(),
                location.getY(),
                location.getZ(),
                direction.getX() * 0.5,
                direction.getY() * 0.5,
                direction.getZ() * 0.5
        );

        world.addEntity(flare);
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