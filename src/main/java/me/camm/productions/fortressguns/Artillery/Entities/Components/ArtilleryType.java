package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import me.camm.productions.fortressguns.Util.DataLoading.Adapters.*;
import org.bukkit.ChatColor;

public enum ArtilleryType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED, LightArtillery.class, "fieldLight", AdapterLightArtillery.class),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE, HeavyArtillery.class,"fieldHeavy", AdapterHeavyArtillery.class),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN, HeavyFlak.class,"heavyFlak", AdapterHeavyFlak.class),
    RAIL_GUN(ChatColor.GRAY+"Rail Gun"+ChatColor.DARK_PURPLE,null,"railGun", AdapterRailGun.class),
    MISSILE_LAUNCHER(ChatColor.GRAY+"Missile Launcher"+ChatColor.BLUE, MissileLauncher.class, "missileLauncher", AdapterMissileLauncher.class),
    HEAVY_MACHINE(ChatColor.GRAY+"Heavy Machine Gun", HeavyMachineGun.class, "heavyMachineGun", AdapterHeavyMachinegun.class),

    CRAM(ChatColor.GRAY+"CRAM", CRAM.class, "cram",AdapterCRAM.class),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW, LightFlak.class,"lightFlak", AdapterLightFlak.class);

    private final String name;
    private final String id;
    private final Class<? extends Artillery> clazz;

    private final Class<? extends AdapterArtillery> adapter;



    ArtilleryType(String name, Class<? extends Artillery> clazz, String id, Class<? extends AdapterArtillery > adapter){
        this.name = name;
        this.clazz = clazz;
        this.id = id;
        this.adapter = adapter;
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

    public Class<? extends AdapterArtillery> getAdapter() {
        return adapter;
    }

}
