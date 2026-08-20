package me.camm.productions.fortressguns.interact.behaviour.ItemBehaviour;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.interact.InteractionBehaviourItem;
import net.minecraft.server.level.WorldServer;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.meta.CrossbowMeta;

public class IBFlare implements InteractionBehaviourItem {

    private static final String FLARE_NAME = ChatColor.GRAY + "Flares";
    private static final NamespacedKey LOADED_FLARE_KEY =
            new NamespacedKey(FortressGuns.getInstance(), "loaded_flare");

    @Override
    public Material[] getLabels() {
        return new Material[] {
                Material.FIREWORK_ROCKET,
                Material.CROSSBOW // for crossbow intervention
        };
    }

    private void fireFlare(Block block) {
        Directional directional =
                (Directional) block.getBlockData();

        BlockFace face = directional.getFacing();

        Location location = block.getLocation()
                .add(0.5, 0.5, 0.5)
                .add(face.getModX() * 0.6,
                        face.getModY() * 0.6,
                        face.getModZ() * 0.6);

        WorldServer world = ((CraftWorld) block.getWorld()).getHandle();

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

    private boolean isFlareLoaded(ItemStack crossbow) {
        if (crossbow == null || crossbow.getType() != Material.CROSSBOW) {
            return false;
        }

        ItemMeta meta = crossbow.getItemMeta();

        return meta != null
                && meta.getPersistentDataContainer().has(
                LOADED_FLARE_KEY,
                PersistentDataType.BYTE
        );
    }

    private void setFlareLoaded(ItemStack crossbow, boolean loaded) {
        ItemMeta meta = crossbow.getItemMeta();

        if (meta == null) {
            return;
        }

        if (loaded) {
            meta.getPersistentDataContainer().set(
                    LOADED_FLARE_KEY,
                    PersistentDataType.BYTE,
                    (byte) 1
            );
        } else {
            meta.getPersistentDataContainer().remove(LOADED_FLARE_KEY);
        }

        crossbow.setItemMeta(meta);
    }

    private void startFlareLoadingCheck(Player player) {
        ItemStack crossbow = player.getInventory().getItemInMainHand();

        if (crossbow.getType() != Material.CROSSBOW) {
            return;
        }

        if (isFlareLoaded(crossbow)) {
            return;
        }

        ItemStack flare = player.getInventory().getItemInOffHand();

        if (!accept(new Tuple2<>(player, flare))) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack current =
                        player.getInventory().getItemInMainHand();

                if (current.getType() != Material.CROSSBOW) {
                    cancel();
                    return;
                }

                CrossbowMeta meta = (CrossbowMeta) current.getItemMeta();

                if (meta == null) {
                    cancel();
                    return;
                }

                // crossbow finished loading
                if (meta.hasChargedProjectiles()) {
                    ItemStack offhand =
                            player.getInventory().getItemInOffHand();

                    if (!accept(new Tuple2<>(player, offhand))) {
                        cancel();
                        return;
                    }

                    offhand.setAmount(offhand.getAmount() - 1);
                    setFlareLoaded(current, true);

                    cancel();
                }
            }
        }.runTaskTimer(
                FortressGuns.getInstance(),
                1L,
                1L
        );
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() == Material.CROSSBOW) {
            startFlareLoadingCheck(player);
            return;
        }

        cancelIfCustomFlare(event);
    }

    @Override
    public void onRCBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() == Material.CROSSBOW) {
            startFlareLoadingCheck(player);
            return;
        }

        cancelIfCustomFlare(event);
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

        if (event.getItem().getType() == Material.CROSSBOW) {
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

        ItemStack crossbow = event.getBow();

        if (crossbow == null || crossbow.getType() != Material.CROSSBOW) {
            return;
        }

        if (!isFlareLoaded(crossbow)) {
            return;
        }

        event.setCancelled(true);

        // consume loaded flare
        setFlareLoaded(crossbow, false);

        // explode because balancing a crossbow sucks
        player.getWorld().createExplosion(
                player.getLocation(),
                4.0F,
                true,
                false
        ); // i've considered making it explode 3 times if the crossbow has multishot but I think that's too cruel

        // set the player on fire
        player.setFireTicks(100);
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

        if (stack == null) {
            return false;
        }

        // Custom flare item
        if (stack.getType() == Material.FIREWORK_ROCKET) {
            ItemMeta meta = stack.getItemMeta();

            return meta != null
                    && meta.hasDisplayName()
                    && FLARE_NAME.equals(meta.getDisplayName());
        }

        // Crossbow loaded with a custom flare
        if (stack.getType() == Material.CROSSBOW) {
            return isFlareLoaded(stack);
        }

        return false;
    }
}