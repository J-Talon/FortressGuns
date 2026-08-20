package me.camm.productions.fortressguns.interact.item.classification.tools;

import me.camm.productions.fortressguns.interact.item.classification.FGItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FGTacticalPointerItem extends FGItem<Void> {

    @Override
    protected ItemStack generate() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(getDisplayName());
        stick.setItemMeta(meta);
        return stick;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY+""+ChatColor.BOLD+"Tactical Pointer";
    }

}
