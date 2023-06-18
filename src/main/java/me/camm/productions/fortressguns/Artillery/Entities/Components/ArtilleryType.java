package me.camm.productions.fortressguns.Artillery.Entities.Components;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import org.bukkit.ChatColor;

public enum ArtilleryType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED, LightArtillery.class, "fieldLight"),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE, HeavyArtillery.class,"fieldHeavy"),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN, HeavyFlak.class,"heavyFlak"),
    RAIL_GUN(ChatColor.GRAY+"Rail Gun"+ChatColor.DARK_PURPLE,null,"railGun"),
    MISSILE_LAUNCHER(ChatColor.GRAY+"Missile Launcher"+ChatColor.BLUE,null, "missileLauncher"),
    HEAVY_MACHINE(ChatColor.GRAY+"Heavy Machine Gun", HeavyMachineGun.class, "heavyMachineGun"),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW, LightFlak.class,"lightFlak");

    private final String name;
    private final String id;
    private final Class<? extends Artillery> clazz;



    ArtilleryType(String name, Class<? extends Artillery> clazz, String id){
        this.name = name;
        this.clazz = clazz;
        this.id = id;
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

}
