package me.camm.productions.fortressguns.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Artillery;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ArtilleryItemCreator
{
    public static ItemStack toItem(Artillery artillery){
        ItemStack stack = new ItemStack(Material.CHEST);

        ItemMeta meta = stack.getItemMeta();
        Objects.requireNonNull(meta);
        meta.setDisplayName(artillery.getType().getName());
        stack.setItemMeta(meta);
        return stack;
    }

    public static void packageArtillery(Artillery artillery){
        Block pack = artillery.getPivot().getEyeLocation().getBlock();
        if (pack.isPassable()) {
            pack.setType(Material.CHEST);
            Chest chest = (Chest)pack.getState();
            Inventory inv = chest.getInventory();
            inv.addItem(toItem(artillery));
        }
        else {
            World world = artillery.getWorld();
            world.dropItem(artillery.getPivot().getEyeLocation(),toItem(artillery));
        }
    }
}
