package me.camm.productions.fortressguns.item.classification.box;

import me.camm.productions.fortressguns.item.classification.FGSingleConsumable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public abstract class FGBoxItem extends FGSingleConsumable {

    @Override
    public @NotNull Material getMaterial() {
        return Material.CHEST;
    }
}
