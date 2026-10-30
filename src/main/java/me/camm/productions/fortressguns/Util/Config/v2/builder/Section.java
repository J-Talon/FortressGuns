package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

public enum Section {

    FIELD_HEAVY("fieldHeavy", new ArtilleryBuilder.FieldHeavyBuilder()),
    FIELD_LIGHT("fieldLight", new ArtilleryBuilder.FieldLightBuilder()),
    LIGHT_FLAK("lightFlak", new ArtilleryBuilder.LightFlakBuilder()),
    HEAVY_FLAK("heavyFlak", new ArtilleryBuilder.HeavyFlakBuilder()),
    HMG("heavyMachineGun", new ArtilleryBuilder.HMGBuilder()),
    MISSILE_LAUNCHER("missileLauncher", new ArtilleryBuilder.MissileLauncherBuilder()),

    BIG_FLAK_SHELL("shellFlak", new ProjectileBuilder.FlakBuilder()),
    SMALL_FLAK_SHELL("lightFlakBullet", new ProjectileBuilder.LightFlakBulletBuilder()),
    HMG_BULLET("HMGBullet", new ProjectileBuilder.HMGBulletBuilder()),
    BIG_SOLID_SHELL("solidShell", new ProjectileBuilder.ShellSolidBuilder()),
    BIG_HE_SHELL("shellHE", new ProjectileBuilder.ShellHeBuilder()),
    HEAT_MISSILE("heatSeekerMissile", new ProjectileBuilder.HeatMissileBuilder());


    private final String section;
    private final SectionBuilder<? extends ConfigSection> builder;

    private Section(String section, SectionBuilder<? extends ConfigSection> builder) {
        this.builder = builder;
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    public SectionBuilder<? extends ConfigSection> getBuilder() {
        return builder;
    }
}
