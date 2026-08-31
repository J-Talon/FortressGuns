package me.camm.productions.fortressguns.interact.item.classification;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public abstract class FGSingleConsumable extends FGItem<Void> {

    public abstract @NotNull Material getMaterial();

    @Override
    protected ItemStack generate() {
        ItemStack stack = new ItemStack(getMaterial());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getDisplayName());
        stack.setItemMeta(meta);
        return stack;
    }
}
