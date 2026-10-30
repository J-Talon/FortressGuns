package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.interact.item.classification.box.FGBoxItem;

public enum ConstructType {
    FIELD_LIGHT(FGItems.FIELD_LIGHT,new ConstructFactory.FactoryLightArtillery(), "fieldLight"),
    FIELD_HEAVY(FGItems.FIELD_HEAVY, new ConstructFactory.FactoryHeavyArtillery(),"fieldHeavy"),
    FLAK_HEAVY(FGItems.FLAK_HEAVY,new ConstructFactory.FactoryHeavyFlak(),"heavyFlak"),
    RAIL_GUN(FGItems.RAILGUN,null,"railGun"),
    MISSILE_LAUNCHER(FGItems.MISSILE_LAUNCHER, new ConstructFactory.FactoryMissileLauncher(), "missileLauncher"),
    HEAVY_MACHINE(FGItems.HMG, new ConstructFactory.FactoryHMG(), "heavyMachineGun"),
    CRAM(FGItems.CRAM, new ConstructFactory.FactoryCRAM(), "cram"),
    FLAK_LIGHT(FGItems.FLAK_LIGHT, new ConstructFactory.FactoryLightFlak(),"lightFlak"),
    DEBUG(null, new ConstructFactory.FactoryTest(), "debug");

    private final FGBoxItem boxItem;
    private final String id;
    private final ConstructFactory<? extends Construct> instantiator;


    ConstructType(FGBoxItem box, ConstructFactory<? extends Construct> factory, String id){
        this.boxItem = box;
        this.instantiator = factory;
        this.id = id;
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

}
