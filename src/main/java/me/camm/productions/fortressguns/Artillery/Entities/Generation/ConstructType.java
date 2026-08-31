package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.Serialization.Config.*;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.interact.item.classification.box.FGBoxItem;

public enum ConstructType {
    FIELD_LIGHT(FGItems.FIELD_LIGHT,new ConstructFactory.FactoryLightArtillery(), "fieldLight", ConfigLightArtillery.class),
    FIELD_HEAVY(FGItems.FIELD_HEAVY, new ConstructFactory.FactoryHeavyArtillery(),"fieldHeavy", ConfigHeavyArtillery.class),
    FLAK_HEAVY(FGItems.FLAK_HEAVY,new ConstructFactory.FactoryHeavyFlak(),"heavyFlak", ConfigHeavyFlak.class),
    RAIL_GUN(FGItems.RAILGUN,null,"railGun", ConfigRailgun.class),
    MISSILE_LAUNCHER(FGItems.MISSILE_LAUNCHER, new ConstructFactory.FactoryMissileLauncher(), "missileLauncher", ConfigMissileLauncher.class),
    HEAVY_MACHINE(FGItems.HMG, new ConstructFactory.FactoryHMG(), "heavyMachineGun", ConfigHeavyMach.class),
    CRAM(FGItems.CRAM, new ConstructFactory.FactoryCRAM(), "cram", ConfigCRAM.class),
    FLAK_LIGHT(FGItems.FLAK_LIGHT, new ConstructFactory.FactoryLightFlak(),"lightFlak", ConfigLightFlak.class),
    DEBUG(null, new ConstructFactory.FactoryTest(), "debug", null);

    private final FGBoxItem boxItem;
    private final String id;
    private final ConstructFactory<? extends Construct> instantiator;

    private final Class<? extends ConfigObject> adapter;

    ConstructType(FGBoxItem box, ConstructFactory<? extends Construct> factory, String id, Class<? extends ConfigObject> o){
        this.boxItem = box;
        this.instantiator = factory;
        this.id = id;
        this.adapter = o;
    }

    public String getName() {
        return boxItem == null ? null : boxItem.getDisplayName();
    }


    public FGBoxItem getBoxItem() {
        return boxItem;
    }

    public String getId(){
        return id;
    }

    public ConstructFactory<? extends Construct> getFactory() {
        return instantiator;
    }

    public Class<? extends ConfigObject> getAdapter() {
        return adapter;
    }

}
