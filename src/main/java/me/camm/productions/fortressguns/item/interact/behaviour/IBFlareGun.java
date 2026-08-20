package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.item.classification.FGItems;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class IBFlareGun implements InteractionBehaviourItem {

    private static final long COOLDOWN_MILLIS = 3000;
    private static final NamespacedKey COOLDOWN_KEY = new NamespacedKey(FortressGuns.getInstance(), "flare_gun_cooldown");

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
        event.setCancelled(true);
    }

    private void fire(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();

        if (isOnCooldown(gun)) {
            player.sendMessage(ChatColor.RED + "Your Flare Gun is on cooldown!");
            return;
        }

        if (!consumeAmmo(player)) {
            player.sendMessage(ChatColor.RED + "You need Flares to fire!");
            return;
        }

        setCooldown(gun);

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
        return FGItems.FLARE_GUN.isSimilar(item.getB());
    }



    private boolean isOnCooldown(ItemStack gun) {

        if (FGItems.FLARE_GUN.isSimilar(gun)) return false;

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


    private void setCooldown(ItemStack gun) {
        ItemMeta meta = gun.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.getPersistentDataContainer().set(
                COOLDOWN_KEY,
                PersistentDataType.LONG,
                System.currentTimeMillis() + COOLDOWN_MILLIS
        );

        gun.setItemMeta(meta);
    }





    private boolean consumeAmmo(Player player) {
        int available = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (FGItems.FLARE.isSimilar(item)) {
                available += item.getAmount();

                if (available >= 1) {
                    break;
                }
            }
        }

        if (available < 1) {
            return false;
        }

        int remaining = 1;

        for (ItemStack item : player.getInventory().getContents()) {
            if (!FGItems.FLARE.isSimilar(item)) {
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

    @Override
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) { // if there is no result, no need to do stuff
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItem(recipe, item)) {
                continue;
            }

            if (FGItems.FLARE_GUN.isSimilar(item)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @Override
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) { // if there is no result, no need to do stuff
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (RecipeManager.recipeUsesItem(recipe, item)) {
                continue;
            }

            if (FGItems.FLARE_GUN.isSimilar(item)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }
}
