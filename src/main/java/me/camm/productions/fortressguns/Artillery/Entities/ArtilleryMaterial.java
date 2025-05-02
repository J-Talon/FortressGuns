package me.camm.productions.fortressguns.Artillery.Entities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ArtilleryMaterial {
    WHEEL(Material.COAL_BLOCK),
    DESERT_BODY(Material.CUT_SANDSTONE),
    OCEAN_BODY(Material.BLUE_TERRACOTTA),
    BASE_SUPPORT(Material.STONE_BRICK_SLAB),
    MACHINERY(Material.WHITE_CONCRETE),
    BARREL(Material.DISPENSER),
    SMALL_BARREL(Material.NETHER_BRICK_FENCE),

    SEAT(Material.BIRCH_TRAPDOOR),
    STANDARD_BODY(Material.GREEN_TERRACOTTA);

    private final Material mat;

    ArtilleryMaterial(Material mat) {
        this.mat = mat;
    }

    public ItemStack asItem(){
        return new ItemStack(mat);
    }

}
