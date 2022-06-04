package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.*;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import org.bukkit.ChatColor;

public enum ArtilleryType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED, LightArtillery.class, true),
    ARTILLERY_GENERIC(ChatColor.GRAY+"Generic"+ChatColor.GOLD, Artillery.class,true),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE, HeavyArtillery.class,true),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN, HeavyFlak.class,true),
    SURFACE_TO_AIR(ChatColor.GRAY+"SAM"+ChatColor.DARK_PURPLE,null,false),
    BARRAGE(ChatColor.GRAY+"SILO"+ChatColor.BLUE,null, true),
    HEAVY_MACHINE(ChatColor.GRAY+"Heavy Machine Gun", HeavyMachineGun.class, true),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW, LightFlak.class,true);

    private final String name;
    private final Class<? extends Artillery> clazz;
    private final boolean seatable;


    ArtilleryType(String name, Class<? extends Artillery> clazz, boolean seatable){
        this.name = name;
        this.clazz = clazz;
        this.seatable = seatable;
    }

    public String getName(){
        return name;
    }

    public Class<? extends Artillery> getClazz() {
        return clazz;
    }

    public boolean isSeatable() {
        return seatable;
    }
}
