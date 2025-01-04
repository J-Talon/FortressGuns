package me.camm.productions.fortressguns.ArtilleryItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public enum AmmoItem {

    STANDARD_HEAVY(Material.LEVER, ChatColor.GRAY+"Standard Shell"),
    EXPLOSIVE_HEAVY(Material.LEVER, ChatColor.GRAY+"High Explosive Shell"),
    FLAK_HEAVY(Material.LEVER, ChatColor.GRAY+"Flak Shell"),
    STANDARD_LIGHT(Material.RAIL,ChatColor.GRAY+"Heavy Caliber Rounds"),
    FLAK_LIGHT(Material.RAIL, ChatColor.GRAY+"Light Flak Rounds"),
    MISSILE(Material.LEVER,ChatColor.GRAY + "Rocket"),
    CRAM(Material.RAIL, ChatColor.GRAY+"CRAM Explosive Rounds");


    private AmmoItem(Material mat, String name) {
        this.mat = mat;
        this.name = name;
    }

    private final Material mat;
    private final  String name;

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }
}
