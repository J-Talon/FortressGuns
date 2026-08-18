package me.camm.productions.fortressguns.Recipes;

import me.camm.productions.fortressguns.item.classification.FGItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.Map;

import static me.camm.productions.fortressguns.item.classification.FGItems.FIELD_LIGHT;

public final class RecipeManager {

    private static final Map<NamespacedKey, Recipe> FG_RECIPES = new HashMap<>();

    private RecipeManager() {
    }

    public static void registerRecipes() {
        ShapedRecipe CRAMBulletRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "cram_bullet_recipe"),
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
                        new NamespacedKey("fortressguns", "flak_shell_recipe"),
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
                        new NamespacedKey("fortressguns", "flare_recipe"),
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
                        new NamespacedKey("fortressguns", "heatseeker_recipe"),
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
                        new NamespacedKey("fortressguns", "he_shell_recipe"),
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
                        new NamespacedKey("fortressguns", "hmgbullet_recipe"),
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
                        new NamespacedKey("fortressguns", "light_flak_bullet_recipe"),
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
                        new NamespacedKey("fortressguns", "solid_shell_recipe"),
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
                        new NamespacedKey("fortressguns", "flare_gun_recipe"),
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
                        new NamespacedKey("fortressguns", "cram_recipe"),
                        FGItems.CRAM.get()
                )
                .shape(
                        "SNS",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.IRON_INGOT)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.BOW)
                .ingredient('S', Material.SPYGLASS)
                .register();

        ShapedRecipe FieldLightRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "field_light_recipe"),
                        FIELD_LIGHT.get()
                )
                .shape(
                        " N ",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.IRON_INGOT)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.BOW)
                .register();
        ShapedRecipe FieldHeavyRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "field_heavy_recipe"),
                        FGItems.FIELD_HEAVY.get()
                )
                .shape(
                        " N ",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.IRON_BLOCK)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.CROSSBOW)
                .register();
        ShapedRecipe FlakHeavyRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "flak_heavy_recipe"),
                        FGItems.FLAK_HEAVY.get()
                )
                .shape(
                        "NNN",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.GOLD_BLOCK)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.CROSSBOW)
                .register();
        ShapedRecipe FlakLightRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "flak_light_recipe"),
                        FGItems.FLAK_LIGHT.get()
                )
                .shape(
                        " N ",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.GOLD_INGOT)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.BOW)
                .register();

        ShapedRecipe MissileLauncherRecipe = RecipeBuilder.create(
                        new NamespacedKey("fortressguns", "missile_launcher_recipe"),
                        FGItems.MISSILE_LAUNCHER.get()
                )
                .shape(
                        "SNS",
                        "NBN",
                        " R "
                )
                .ingredient('N', Material.GOLD_INGOT)
                .ingredient('R', Material.REDSTONE)
                .ingredient('B', Material.CROSSBOW)
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