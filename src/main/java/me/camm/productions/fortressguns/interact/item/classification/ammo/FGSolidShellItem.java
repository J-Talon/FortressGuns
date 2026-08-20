package me.camm.productions.fortressguns.interact.item.classification.ammo;

import me.camm.productions.fortressguns.interact.item.classification.FGSingleConsumable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class FGSolidShellItem extends FGSingleConsumable {
    @Override
    public String getDisplayName() {
        return ChatColor.GRAY+"Solid Shell";
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.LEVER;
    }
}
