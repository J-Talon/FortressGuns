package me.camm.productions.fortressguns.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryCore;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArtilleryItemHelper
{
    private static final String CRATE = "Crate";
    private static final Material CHEST =  Material.CHEST;
    private static final String VALUE = ChatColor.GOLD+"Value: ";

    private static Map<String, Class<? extends Artillery>> artilleryNames;
    private static Map<String, AmmoItem> itemNames;


    private static ItemStack stick = null;

    static {
        artilleryNames = new HashMap<>();
        for (ArtilleryType type: ArtilleryType.values()) {
            artilleryNames.put(type.getName(), type.getClazz());
        }


        itemNames = new HashMap<>();
        for (AmmoItem item: AmmoItem.values()) {
            itemNames.put(item.getName(), item);
        }
    }


    public static ItemStack getStick() {
        if (stick != null)
            return stick;

        stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+""+ChatColor.BOLD+"Tactical Pointer");
        stick.setItemMeta(meta);
        return stick;
    }


    public static @Nullable Class<? extends Artillery> isArtillery(ItemStack stack) {

        if (stack == null)
            return null;

        if (stack.getType() != Material.CHEST)
            return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return null;

        return artilleryNames.getOrDefault(meta.getDisplayName(), null);

    }

    public static @Nullable AmmoItem isAmmoItem(ItemStack stack) {

        if (stack == null)
            return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return null;

        String name = meta.getDisplayName();

        return itemNames.getOrDefault(name, null);
    }

    public static boolean matchesName(ItemStack first, ItemStack second) {

        if (first == null || second == null)
            return false;

        if (!first.hasItemMeta() || !second.hasItemMeta())
            return false;

        //has item meta literally compares it to null
        return first.getItemMeta().getDisplayName().equals(second.getItemMeta().getDisplayName());
    }


    public static boolean matchesName(ItemStack first, String second) {

        if (first == null || second == null)
            return false;

        if (!first.hasItemMeta())
            return false;

        return first.getItemMeta().getDisplayName().equals(second);
    }



    public static ItemStack toItem(Artillery artillery) throws IllegalStateException {
        ItemStack stack = new ItemStack(CHEST);

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
         throw new IllegalStateException("Stack meta is null!");

        meta.setDisplayName(artillery.getType().getName());
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack createAmmoItem(AmmoItem item) throws IllegalStateException {
        ItemStack stack = new ItemStack(item.getMat());
        ItemMeta meta = stack.getItemMeta();

        if (meta == null)
            throw new IllegalStateException("Stack meta is null!");

        meta.setDisplayName(item.getName());

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

        World bukkit = artillery.getWorld();
        bukkit.dropItem(loc,toItem(artillery));

    }
}
