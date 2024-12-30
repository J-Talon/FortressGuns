package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.*;
import org.bukkit.ChatColor;

public enum ArtilleryType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED, LightArtillery.class, "fieldLight", ConfigLightArtillery.class),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE, HeavyArtillery.class,"fieldHeavy", ConfigHeavyArtillery.class),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN, HeavyFlak.class,"heavyFlak", ConfigHeavyFlak.class),
    RAIL_GUN(ChatColor.GRAY+"Rail Gun"+ChatColor.DARK_PURPLE,null,"railGun", ConfigRailgun.class),
    MISSILE_LAUNCHER(ChatColor.GRAY+"Missile Launcher"+ChatColor.BLUE, MissileLauncher.class, "missileLauncher", ConfigMissileLauncher.class),
    HEAVY_MACHINE(ChatColor.GRAY+"Heavy Machine Gun", HeavyMachineGun.class, "heavyMachineGun", ConfigHeavyMach.class),

    CRAM(ChatColor.GRAY+"CRAM", CRAM.class, "cram", ConfigCRAM.class),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW, LightFlak.class,"lightFlak", ConfigLightFlak.class);

    private final String name;
    private final String id;
    private final Class<? extends Artillery> clazz;

    private final Class<? extends ConfigObject> adapter;



    ArtilleryType(String name, Class<? extends Artillery> clazz, String id, Class<? extends ConfigObject> o){
        this.name = name;
        this.clazz = clazz;
        this.id = id;
        this.adapter = o;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public Class<? extends Artillery> getClazz() {
        return clazz;
    }

    public Class<? extends ConfigObject> getAdapter() {
        return adapter;
    }

}
