package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Recipes.RecipeManager;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.item.ItemUtils;
import me.camm.productions.fortressguns.item.classification.FGItems;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class IBConstructBox implements InteractionBehaviourItem {

    @Override
    public boolean accept(Tuple2<Player, ItemStack> tup) {
        ItemStack stack = tup.getB();
        return ItemUtils.holdsConstruct(stack) != null;
    }


    @Override
    public Material[] getLabels() {
        return new Material[]{Material.CHEST};
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED+"[!] Right click the air if you're trying to assemble artillery.");
    }


    @Override
    public void onRCAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        //tad bit inefficient
        ConstructType type = ItemUtils.holdsConstruct(stack);
        if (type == null)
            return;

        if (player.isFlying() || !player.getLocation().clone().subtract(0,0.1,0).getBlock().getType().isSolid()) {
            player.sendMessage(ChatColor.RED+"[!] You must be on the ground to assemble this.");
            return;
        }

        Location eyeLoc = player.getEyeLocation();

        int x = (int)(Math.toRadians(eyeLoc.getPitch()) * 100);
        int z = (int)(Math.toRadians(eyeLoc.getYaw()) * 100);

        ConstructFactory<? extends Construct> factory = type.getFactory();


        double offsetY = -0.6;
        if (type == ConstructType.MISSILE_LAUNCHER) offsetY -= 0.75;

        Construct cons = factory.create(player.getLocation().add(0,offsetY,0), type.ordinal(),x,z, 0);

        if (cons != null) {
            boolean success = cons.spawn();
            ChunkLoader.addActivePiece(cons);
            if (!success)
                player.sendMessage(ChatColor.RED+"[!] There is not enough space here to assemble this.");
            else
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE,1,1);
        }
        else {
            player.sendMessage(ChatColor.RED+"[!] Unable to create construct. This is probably a bug.");
        }

        event.setCancelled(true);


    }

    @Override
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            // this custom item is legitimately required by the recipe.
            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (accept(new Tuple2<>(null, item))) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @Override
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        Recipe recipe = event.getRecipe();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            // this custom item is legitimately required by the recipe.
            if (RecipeManager.recipeUsesItemStrictly(recipe, item)) {
                continue;
            }

            if (accept(new Tuple2<>(event.getWhoClicked() instanceof Player player ? player : null, item))) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
