package me.camm.productions.fortressguns.item;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.AmmoItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemUtils
{
    private static final Map<String, ConstructType> constructs;
    private static final Map<String, AmmoItem> itemNames;


    static {
        constructs = new HashMap<>();
        for (ConstructType type: ConstructType.values()) {
            constructs.put(type.getName(), type);
        }


        itemNames = new HashMap<>();
        for (AmmoItem item: AmmoItem.values()) {
            itemNames.put(item.getName(), item);
        }
    }



    public static @Nullable ConstructType holdsConstruct(ItemStack stack) {

        if (stack == null)
            return null;

        if (stack.getType() != Material.CHEST)
            return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return null;

        return constructs.getOrDefault(meta.getDisplayName(), null);

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



    public static ItemStack createAmmoItem(AmmoItem item) throws IllegalStateException {
        ItemStack stack = new ItemStack(item.getMat());
        ItemMeta meta = stack.getItemMeta();

        if (meta == null)
            throw new IllegalStateException("Stack meta is null!");

        meta.setDisplayName(item.getName());

        stack.setItemMeta(meta);
        return stack;
    }



    public static void packageArtillery(Artillery artillery) throws IllegalStateException {

        ArmorStand pivot = (ArmorStand)artillery.getCoreEntity();
        Location loc = pivot.getEyeLocation();

        World bukkit = artillery.getWorld();
        bukkit.dropItem(loc,artillery.getType().getBoxItem().get());

    }
}
