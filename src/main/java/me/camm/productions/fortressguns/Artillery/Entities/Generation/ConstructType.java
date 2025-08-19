package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Util.DataLoading.Config.*;
import org.bukkit.ChatColor;

public enum ConstructType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED, new ConstructFactory.FactoryLightArtillery(), "fieldLight", ConfigLightArtillery.class),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE, new ConstructFactory.FactoryHeavyArtillery(),"fieldHeavy", ConfigHeavyArtillery.class),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN, new ConstructFactory.FactoryHeavyFlak(),"heavyFlak", ConfigHeavyFlak.class),
    RAIL_GUN(ChatColor.GRAY+"Rail Gun"+ChatColor.DARK_PURPLE,null,"railGun", ConfigRailgun.class),
    MISSILE_LAUNCHER(ChatColor.GRAY+"Missile Launcher"+ChatColor.BLUE, new ConstructFactory.FactoryMissileLauncher(), "missileLauncher", ConfigMissileLauncher.class),
    HEAVY_MACHINE(ChatColor.GRAY+"Heavy Machine Gun", new ConstructFactory.FactoryHMG(), "heavyMachineGun", ConfigHeavyMach.class),

    CRAM(ChatColor.GRAY+"CRAM", null, "cram", ConfigCRAM.class),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW, new ConstructFactory.FactoryLightFlak(),"lightFlak", ConfigLightFlak.class);

    private final String name;
    private final String id;
    private final ConstructFactory<? extends Construct> instantiator;

    private final Class<? extends ConfigObject> adapter;

    ConstructType(String name, ConstructFactory<? extends Construct> factory, String id, Class<? extends ConfigObject> o){
        this.name = name;
        this.instantiator = factory;
        this.id = id;
        this.adapter = o;
    }

    public String getName(){
        return name;
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
