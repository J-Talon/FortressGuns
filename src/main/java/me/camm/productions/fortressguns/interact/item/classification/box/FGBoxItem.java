package me.camm.productions.fortressguns.interact.item.classification.box;

import me.camm.productions.fortressguns.interact.item.classification.FGSingleConsumable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FGBoxItem extends FGSingleConsumable {

    @Override
    public @NotNull Material getMaterial() {
        return Material.CHEST;
    }


    @Override
    public boolean isSimilar(@Nullable ItemStack other) {

        if (other == null || other.getItemMeta() == null)
            return false;

        ItemStack comp = get();
        if (comp.getType() != other.getType()) return false;

        if (comp.getItemMeta() == null)
            return super.isSimilar(other);

        return comp.getItemMeta().getDisplayName().equals(other.getItemMeta().getDisplayName());
    }


    @Override
    public ItemStack dynamicGet(@Nullable String context) {
        ItemStack stack = get().clone();
        if (context == null) return stack;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return stack;

        meta.setLore(List.of(context));
        stack.setItemMeta(meta);
        return stack;
    }
}
