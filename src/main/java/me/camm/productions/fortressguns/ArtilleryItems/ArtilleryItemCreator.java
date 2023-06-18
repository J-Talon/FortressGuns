package me.camm.productions.fortressguns.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArtilleryItemCreator
{
    private static final String CRATE = "Crate";
    private static final Material CHEST =  Material.CHEST;

    public static ItemStack toItem(Artillery artillery) throws IllegalStateException {
        ItemStack stack = new ItemStack(CHEST);

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
         throw new IllegalStateException("Stack meta is null!");

        meta.setDisplayName(artillery.getType().getName());
        stack.setItemMeta(meta);

        return stack;
    }







    public static ItemStack createArtilleryItem(ArtilleryType type) throws IllegalStateException {
        ItemStack stack = new ItemStack(CHEST);
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("Stack meta is null!");
        }

        meta.setDisplayName(type.getName());
        stack.setItemMeta(meta);
        return stack;
    }





    public static void packageArtillery(Artillery artillery) throws IllegalStateException {


        ArtilleryCore pivot = artillery.getPivot();
        Location loc = pivot.getEyeLocation();


        Block pack = loc.getBlock();
        if (pack.isPassable()) {
            pack.setType(Material.CHEST);
            Chest chest = (Chest)pack.getState();
            chest.setCustomName(CRATE);

            Inventory inv = chest.getInventory();
            inv.addItem(toItem(artillery));
        }
        else {
            World bukkit = artillery.getWorld();
            bukkit.dropItem(loc,toItem(artillery));
        }
    }
}
