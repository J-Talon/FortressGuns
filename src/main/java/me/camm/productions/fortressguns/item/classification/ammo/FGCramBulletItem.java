package me.camm.productions.fortressguns.item.classification.ammo;

import me.camm.productions.fortressguns.item.classification.FGSingleConsumable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class FGCramBulletItem extends FGSingleConsumable {

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY+"CRAM Explosive Rounds";
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.RAIL;
    }
}
