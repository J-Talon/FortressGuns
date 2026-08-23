package me.camm.productions.fortressguns.item.classification.ingredients;

import me.camm.productions.fortressguns.item.classification.FGItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FGSimpleIngredient extends FGItem<Void> { // Things whose only purpose is to be a crafting ingredient
    private Material material;
    private String name;
    
    protected FGSimpleIngredient(Material material, String name) {
        this.material = material;
        this.name = name;
    }

    @Override
    protected ItemStack generate() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public String getDisplayName() {
        return name;
    }
}
