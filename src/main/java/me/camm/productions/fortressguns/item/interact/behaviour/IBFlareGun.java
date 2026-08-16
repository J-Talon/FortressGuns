package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.ArtilleryItems.FlareGun;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class IBFlareGun implements InteractionBehaviourItem {
    @Override
    public Material[] getLabels() {
        return new Material[]{Material.DISPENSER};
    }

    @Override
    public void onRCAir(PlayerInteractEvent event) {
        fire(event);
    }

    @Override
    public void onRCBlock(PlayerInteractEvent event) {
        fire(event);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (FlareGun.isFlareGun(gun)) {
            event.setCancelled(true);
        }
    }

    private void fire(PlayerInteractEvent event) {
        Player player = event.getPlayer();
//        event.setCancelled(true);

        ItemStack gun = player.getInventory().getItemInMainHand();

        if (!FlareGun.isFlareGun(gun)) {
            return;
        }

        if (FlareGun.isOnCooldown(gun)) {
            player.sendMessage(ChatColor.RED + "Your Flare Gun is on cooldown!");
            return;
        }

        if (!FlareGun.consumeAmmo(player, 1)) {
            player.sendMessage(ChatColor.RED + "You need Flares to fire!");
            return;
        }

        FlareGun.setCooldown(player, gun);

        for (int i = 0; i < 5; i++) {
            fireProjectile(player);
        }
    }

    private void fireProjectile(Player player) {
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

    @Override
    public boolean accept(Tuple2<Player, ItemStack> item) {
        return FlareGun.isFlareGun(item.getB());
    }
}