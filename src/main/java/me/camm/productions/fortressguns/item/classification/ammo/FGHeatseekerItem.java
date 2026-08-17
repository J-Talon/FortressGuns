package me.camm.productions.fortressguns.item.classification.ammo;

import me.camm.productions.fortressguns.item.classification.FGSingleConsumable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class FGHeatseekerItem extends FGSingleConsumable {

    @Override
    public @NotNull Material getMaterial() {
        return Material.LEVER;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY + "Heat Seeking Missile";
    }
}
