package me.camm.productions.fortressguns.item.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FlareGun implements Listener {

    private static final String name = "Flare Gun";
    private static final Material material = Material.DISPENSER;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public static boolean isFlareGun(ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta != null
                && meta.hasDisplayName()
                && name.equals(meta.getDisplayName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        if (!isFlareGun(player.getInventory().getItemInMainHand())) {
            return;
        }

        event.setCancelled(true);

        fire(player);
    }

    private void fire(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer nmsPlayer = craftPlayer.getHandle();

        WorldServer world = (WorldServer) nmsPlayer.getWorld();

        // Spawn slightly in front of the player's eyes
        org.bukkit.Location location = player.getEyeLocation().clone()
                .add(player.getEyeLocation().getDirection().multiply(0.5));

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
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Flare Gun");
        item.setItemMeta(meta);
        return item;
    }
}