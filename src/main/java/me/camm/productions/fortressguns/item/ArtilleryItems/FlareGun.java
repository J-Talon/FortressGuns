package me.camm.productions.fortressguns.item.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class FlareGun implements Listener {

    private static final String name = "Flare Gun";
    private static final Material material = Material.DISPENSER;
    private static final long COOLDOWN_MILLIS = 3000;
    private static final NamespacedKey FLARE_GUN_KEY = new NamespacedKey("fortressguns", "flare_gun");
    private static final NamespacedKey COOLDOWN_KEY = new NamespacedKey("fortressguns", "flare_gun_cooldown");

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        meta.getPersistentDataContainer().set(
                FLARE_GUN_KEY,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    public static boolean isFlareGun(ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta != null
                && meta.getPersistentDataContainer().has(
                FLARE_GUN_KEY,
                PersistentDataType.BYTE
        );
    }

    private static boolean isFlareAmmo(ItemStack item) {
        if (item == null || item.getType() != AmmoItem.FLARE.getMat()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta != null
                && meta.hasDisplayName()
                && AmmoItem.FLARE.getName().equals(meta.getDisplayName());
    }

    private static boolean consumeAmmo(Player player, int amount) {
        int available = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (isFlareAmmo(item)) {
                available += item.getAmount();

                if (available >= amount) {
                    break;
                }
            }
        }

        if (available < amount) {
            return false;
        }

        int remaining = amount;

        for (ItemStack item : player.getInventory().getContents()) {
            if (!isFlareAmmo(item)) {
                continue;
            }

            int remove = Math.min(item.getAmount(), remaining);

            item.setAmount(item.getAmount() - remove);
            remaining -= remove;

            if (remaining <= 0) {
                break;
            }
        }

        return true;
    }

    private static boolean isOnCooldown(ItemStack gun) {
        ItemMeta meta = gun.getItemMeta();

        if (meta == null) {
            return false;
        }

        Long cooldownUntil = meta.getPersistentDataContainer().get(
                COOLDOWN_KEY,
                PersistentDataType.LONG
        );

        if (cooldownUntil == null) {
            return false;
        }

        if (System.currentTimeMillis() >= cooldownUntil) {
            meta.getPersistentDataContainer().remove(COOLDOWN_KEY);
            gun.setItemMeta(meta);
            return false;
        }

        return true;
    }

    private static void setCooldown(Player player, ItemStack gun) {
        ItemMeta meta = gun.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(
                COOLDOWN_KEY,
                PersistentDataType.LONG,
                System.currentTimeMillis() + COOLDOWN_MILLIS
        );

        gun.setItemMeta(meta);;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack gun = player.getInventory().getItemInMainHand();

        if (!isFlareGun(gun)) {
            return;
        }

        if (isOnCooldown(gun)) {
            player.sendMessage(ChatColor.RED + "Your Flare Gun is on cooldown!");
            return;
        }

        if (!consumeAmmo(player, 1)) {
            player.sendMessage(ChatColor.RED + "You need Flares to fire!");
            return;
        }

        setCooldown(player, gun);

        for (int i = 0; i < 5; i++) {
            fire(player);
        }
    }

    private void fire(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer nmsPlayer = craftPlayer.getHandle();

        WorldServer world = (WorldServer) nmsPlayer.getWorld();

        org.bukkit.Location location = player.getEyeLocation().clone();

        SimpleFlare flare = new SimpleFlare(
                world,
                location.getX(),
                location.getY(),
                location.getZ(),
                nmsPlayer
        );

        world.addEntity(flare);
    }

    public static ItemStack getItem() {
        return createItem();
    }
}