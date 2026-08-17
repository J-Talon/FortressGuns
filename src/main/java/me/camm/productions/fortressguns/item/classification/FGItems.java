package me.camm.productions.fortressguns.item.classification;

import me.camm.productions.fortressguns.item.classification.Items.FGFlareGunItem;
import me.camm.productions.fortressguns.item.classification.Items.FGTacticalPointerItem;
import me.camm.productions.fortressguns.item.classification.ammo.*;
import me.camm.productions.fortressguns.item.classification.box.*;

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




}
