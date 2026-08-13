package me.camm.productions.fortressguns.item.interact.behaviour;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructFactory;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Util.Math.Tuple2;
import me.camm.productions.fortressguns.Util.chunk.ChunkLoader;
import me.camm.productions.fortressguns.item.ArtilleryItems.ItemUtils;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviour;
import me.camm.productions.fortressguns.item.interact.InteractionBehaviourItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
}
