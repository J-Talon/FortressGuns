package me.camm.productions.fortressguns.interact.item.classification.ammo;

import me.camm.productions.fortressguns.interact.item.classification.FGSingleConsumable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class FGHmgBulletItem extends FGSingleConsumable {

    @Override
    public @NotNull Material getMaterial() {
        return Material.RAIL;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GRAY+"Heavy Caliber Rounds";
    }
}
