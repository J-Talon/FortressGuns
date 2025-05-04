package me.camm.productions.fortressguns.Inventory.Abstract;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public enum StaticItem {

    ROTATE_VERTICAL(ChatColor.GOLD+"Rotate Turret Up/down", Material.YELLOW_TERRACOTTA),
    LOAD_UNLOAD(ChatColor.GOLD+"Left click: Load | Right click: Unload", Material.GREEN_TERRACOTTA),
    ROUND_SHOWCASE(ChatColor.GRAY+"", Material.RAIL),

    JAMMED_ROUND(ChatColor.GRAY+"Round", Material.YELLOW_TERRACOTTA),
    RAMROD(ChatColor.WHITE + "Ramrod",Material.STICK),
    ROTATE_HORIZONTAL(ChatColor.GOLD+"Rotate Turret Left/right", Material.YELLOW_TERRACOTTA),
    FIRE(ChatColor.RED+"FIRE!", Material.RED_TERRACOTTA),
    DISASSEMBLE(ChatColor.BLUE+"Disassemble", Material.BLUE_TERRACOTTA),
    INFO(ChatColor.WHITE+"", Material.CYAN_TERRACOTTA),
    BORDER(ChatColor.BLACK+"", Material.BLACK_STAINED_GLASS_PANE),

    CLEAR_JAM(ChatColor.GRAY+"Clear Jam", Material.GRAY_TERRACOTTA),
    RELOAD(ChatColor.GREEN+"Reload", Material.GREEN_TERRACOTTA);

    final String name;
    final Material mat;
    StaticItem(String name, Material mat) {
        this.name = name;
        this.mat = mat;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public Material getMat() {
        return mat;
    }

    public ItemStack toItemRaw() {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName());
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack toItem(String lore) {
        ItemStack stack = new ItemStack(getMat());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName());
        List<String> val = new ArrayList<>();
        val.add(lore);
        meta.setLore(val);
        stack.setItemMeta(meta);
        return stack;
    }
}
