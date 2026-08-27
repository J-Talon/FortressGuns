package me.camm.productions.fortressguns.interact.item.classification.tools;

import me.camm.productions.fortressguns.interact.item.classification.FGItem;
import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class FGFlareGunItem extends FGItem<Void> {

    private static final String name = ChatColor.GRAY + "Flare Gun";
    private static final Material material = Material.DISPENSER;
    private static final NamespacedKey FLARE_GUN_KEY = new NamespacedKey(FortressGuns.getInstance(), "flare_gun");


    @Override
    protected ItemStack generate() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        meta.getPersistentDataContainer().set(
                FLARE_GUN_KEY,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public String getDisplayName() {
        return name;
    }


    @Override
    public boolean isSimilar(@Nullable ItemStack other) {
        if (other == null || other.getType() != material) {
            return false;
        }

        ItemMeta meta = other.getItemMeta();

        return meta != null
                && meta.getPersistentDataContainer().has(
                FLARE_GUN_KEY,
                PersistentDataType.BYTE
        );
    }

}
