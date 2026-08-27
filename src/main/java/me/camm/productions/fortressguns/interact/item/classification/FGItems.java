package me.camm.productions.fortressguns.interact.item.classification;

import me.camm.productions.fortressguns.interact.item.classification.ammo.*;
import me.camm.productions.fortressguns.interact.item.classification.box.*;
import me.camm.productions.fortressguns.interact.item.classification.ingredients.*;
import me.camm.productions.fortressguns.interact.item.classification.tools.FGFlareGunItem;
import me.camm.productions.fortressguns.interact.item.classification.tools.FGTacticalPointerItem;


public class FGItems {

    public static FGItem<Void> TACTICAL_PTR = new FGTacticalPointerItem();


    public static FGItem<Void> FLARE_GUN = new FGFlareGunItem();
    public static FGSingleConsumable FLARE = new FGFlareItem();

    public static FGBoxItem CRAM = new FGCramBox();
    public static FGBoxItem FIELD_HEAVY = new FGFieldHeavyBox();
    public static FGBoxItem FIELD_LIGHT = new FGFieldLightBox();
    public static FGBoxItem FLAK_HEAVY = new FGFlakHeavyBox();
    public static FGBoxItem FLAK_LIGHT = new FGLightFlakBox();
    public static FGBoxItem HMG = new FGHmgBox();
    public static FGBoxItem MISSILE_LAUNCHER = new FGMissileLauncherBox();
    public static FGBoxItem RAILGUN = new FGRailgunBox();

    public static FGSingleConsumable CRAM_BULLET = new FGCramBulletItem();
    public static FGSingleConsumable FLAK_SHELL = new FGFlakShellItem();
    public static FGSingleConsumable HEAT_SEEKER_MISSILE = new FGHeatseekerItem();
    public static FGSingleConsumable HE_SHELL = new FGHEShellItem();
    public static FGSingleConsumable HMG_BULLET = new FGHmgBulletItem();
    public static FGSingleConsumable LIGHT_FLAK_BULLET = new FGLightFlakAmmoItem();
    public static FGSingleConsumable SOLID_SHELL = new FGSolidShellItem();

    public static FGSimpleIngredient ARTILLERY_BASE = new FGArtilleryBase();
    public static FGSimpleIngredient FLAK_BARREL = new FGFlakBarrel();
    public static FGSimpleIngredient FIELD_BARREL = new FGFieldBarrel();
    public static FGSimpleIngredient MISSILE_BARREL = new FGMissileBarrel();
    public static FGSimpleIngredient MACHINE_GUN_BARREL = new FGMachineGunBarrel();
    public static FGSimpleIngredient[] SIMPLE_INGREDIENTS = {ARTILLERY_BASE, FLAK_BARREL, FIELD_BARREL, MISSILE_BARREL, MACHINE_GUN_BARREL};

}
