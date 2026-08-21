package me.camm.productions.fortressguns.Recipes;

import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.item.classification.FGItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Map;

import static me.camm.productions.fortressguns.item.classification.FGItems.FIELD_LIGHT;

public final class RecipeManager {

    private static final Map<NamespacedKey, Recipe> FG_RECIPES = new HashMap<>();

    private RecipeManager() {
    }

    public static boolean recipeUsesItemStrictly(Recipe recipe, ItemStack item) {
        if (recipe instanceof ShapedRecipe shaped) {
            for (RecipeChoice choice : shaped.getChoiceMap().values()) {
                if (choice instanceof RecipeChoice.ExactChoice exact) {
                    for (ItemStack choiceItem : exact.getChoices()) {
                        if (item.isSimilar(choiceItem)) {
                            return true;
                        }
                    }
                }
            }
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            for (RecipeChoice choice : shapeless.getChoiceList()) {
                if (choice instanceof RecipeChoice.ExactChoice exact) {
                    for (ItemStack choiceItem : exact.getChoices()) {
                        if (item.isSimilar(choiceItem)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static void registerRecipes() {
        ShapedRecipe ArtilleryBaseRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "artillery_base_recipe"),
                        FGItems.ARTILLERY_BASE.get()
                )
                .shape(
                        " B ",
                        "BBB",
                        " B "
                )
                .ingredient('B', Material.STONE_BRICKS)
                .register();

        ShapedRecipe FieldBarrelRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "field_barrel_recipe"),
                        FGItems.FIELD_BARREL.get()
                )
                .shape(
                        "IDI",
                        "D D",
                        "IDI"
                )
                .ingredient('D', Material.DISPENSER)
                .ingredient('I', Material.IRON_INGOT)
                .register();

        ShapedRecipe FlakBarrelRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flak_barrel_recipe"),
                        FGItems.FLAK_BARREL.get()
                )
                .shape(
                        "CCC",
                        "DDD",
                        "CCC"
                )
                .ingredient('D', Material.DISPENSER)
                .ingredient('C', Material.COPPER_INGOT)
                .register();

        ShapedRecipe MachineGunBarrelRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "machine_gun_barrel_recipe"),
                        FGItems.MACHINE_GUN_BARREL.get()
                )
                .shape(
                        "DDR",
                        "DDR",
                        "DDR"
                )
                .ingredient('D', Material.DISPENSER)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe MissileBarrelRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "missile_barrel_recipe"),
                        FGItems.MISSILE_BARREL.get()
                )
                .shape(
                        "DRD",
                        "DDD",
                        "RRR"
                )
                .ingredient('D', Material.DISPENSER)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe CRAMBulletRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "cram_bullet_recipe"),
                        FGItems.CRAM_BULLET.get()
                )
                .shape(
                        " I ",
                        "INI",
                        " G "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('G', Material.GUNPOWDER)
                .ingredient('N', Material.IRON_NUGGET)
                .register();

        ShapedRecipe FlakShellRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flak_shell_recipe"),
                        FGItems.FLAK_SHELL.get()
                )
                .shape(
                        " I ",
                        "ITI",
                        " G "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('G', Material.GUNPOWDER)
                .ingredient('T', Material.TNT)
                .register();

        ShapedRecipe flareRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flare_recipe"),
                        FGItems.FLARE.get()
                )
                .shape(
                        " R ",
                        "RFR",
                        " R "
                )
                .ingredient('R', Material.REDSTONE)
                .ingredient('F', Material.FIREWORK_ROCKET)
                .register();

        ShapedRecipe HeatseekerRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "heatseeker_recipe"),
                        FGItems.HEAT_SEEKER_MISSILE.get()
                )
                .shape(
                        " S ",
                        "ICI",
                        "FFF"
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('F', Material.FIREWORK_ROCKET)
                .ingredient('C', Material.COMPASS)
                .ingredient('S', Material.SPYGLASS)
                .register();

        ShapedRecipe HEShellRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "he_shell_recipe"),
                        FGItems.HE_SHELL.get()
                )
                .shape(
                        " I ",
                        "ITI",
                        "ITI"
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('T', Material.TNT)
                .register();

        ShapedRecipe HMGBulletRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "hmgbullet_recipe"),
                        FGItems.HMG_BULLET.get()
                )
                .shape(
                        " I ",
                        " C ",
                        " I "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('C', Material.GUNPOWDER)
                .register();

        ShapedRecipe LightFlakBulletRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "light_flak_bullet_recipe"),
                        FGItems.LIGHT_FLAK_BULLET.get()
                )
                .shape(
                        " S ",
                        "ICI",
                        " I "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('C', Material.IRON_BLOCK)
                .ingredient('S', Material.DIAMOND)
                .register();

        ShapedRecipe SolidShellRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "solid_shell_recipe"),
                        FGItems.SOLID_SHELL.get()
                )
                .shape(
                        " S ",
                        "III",
                        " I "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('S', Material.IRON_NUGGET)
                .register();

        ShapedRecipe flareGunRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flare_gun_recipe"),
                        FGItems.FLARE_GUN.get()
                )
                .shape(
                        "III",
                        "I I",
                        " R "
                )
                .ingredient('I', Material.IRON_INGOT)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe CRAMRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "cram_recipe"),
                        FGItems.CRAM.get()
                )
                .shape(
                        " M ",
                        "SBS",
                        "BBB"
                )
                .ingredient('M', FGItems.MACHINE_GUN_BARREL)
                .ingredient('B', FGItems.ARTILLERY_BASE)
                .ingredient('S', Material.SPYGLASS)
                .register();

        ShapedRecipe HMGRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "hmg_recipe"),
                        FGItems.HMG.get()
                )
                .shape(
                        " M ",
                        " B ",
                        "B B"
                )
                .ingredient('M', FGItems.MACHINE_GUN_BARREL)
                .ingredient('B', FGItems.ARTILLERY_BASE)
                .register();

        ShapedRecipe FieldLightRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "field_light_recipe"),
                        FIELD_LIGHT.get()
                )
                .shape(
                        " F ",
                        " B ",
                        " B "
                )
                .ingredient('F', FGItems.FIELD_BARREL)
                .ingredient('B', FGItems.ARTILLERY_BASE)
                .register();

        ShapedRecipe FieldHeavyRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "field_heavy_recipe"),
                        FGItems.FIELD_HEAVY.get()
                )
                .shape(
                        " B ",
                        "RBR",
                        "AAA"
                )
                .ingredient('A', FGItems.ARTILLERY_BASE)
                .ingredient('B', FGItems.FIELD_BARREL)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe FlakHeavyRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flak_heavy_recipe"),
                        FGItems.FLAK_HEAVY.get()
                )
                .shape(
                        " B ",
                        "ABA",
                        "AAA"
                )
                .ingredient('A', FGItems.ARTILLERY_BASE)
                .ingredient('B', FGItems.FLAK_BARREL)
                .register();

        ShapedRecipe FlakLightRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "flak_light_recipe"),
                        FGItems.FLAK_LIGHT.get()
                )
                .shape(
                        " B ",
                        " A ",
                        "ARA"
                )
                .ingredient('A', FGItems.ARTILLERY_BASE)
                .ingredient('B', FGItems.FLAK_BARREL)
                .ingredient('R', Material.REDSTONE)
                .register();

        ShapedRecipe MissileLauncherRecipe = RecipeBuilder.create(
                        new NamespacedKey(FortressGuns.getInstance(), "missile_launcher_recipe"),
                        FGItems.MISSILE_LAUNCHER.get()
                )
                .shape(
                        "SCS",
                        "MBM",
                        "BBB"
                )
                .ingredient('B', FGItems.ARTILLERY_BASE)
                .ingredient('M', FGItems.MISSILE_BARREL)
                .ingredient('C', Material.COMPASS)
                .ingredient('S', Material.SPYGLASS)
                .register();

    }

    public static void register(NamespacedKey key, Recipe recipe) {
        FG_RECIPES.put(key, recipe);
        Bukkit.addRecipe(recipe);
    }

    public static boolean isCustomRecipe(Recipe recipe) {

        if (recipe instanceof ShapedRecipe shaped) {
            return FG_RECIPES.containsKey(shaped.getKey());
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            return FG_RECIPES.containsKey(shapeless.getKey());
        }

        return false;
    }
}