package me.camm.productions.fortressguns.Artillery;

import org.bukkit.ChatColor;

public enum ArtilleryType {
    FIELD_LIGHT(ChatColor.GRAY+"Field Light"+ChatColor.RED),
    ARTILLERY_GENERIC(ChatColor.GRAY+"Generic"+ChatColor.GOLD),
    FIELD_HEAVY(ChatColor.GRAY+"Field Heavy"+ChatColor.WHITE),
    FLAK_HEAVY(ChatColor.GRAY+"Heavy Flak"+ChatColor.GREEN),
    MISSILE(ChatColor.GRAY+"Missile Launcher"+ChatColor.DARK_PURPLE),
    FLAK_LIGHT(ChatColor.GRAY+"Light Flak"+ChatColor.YELLOW);

    private final String name;


    ArtilleryType(String name){
        this.name = name;

    }

    public String getName(){
        return name;
    }

}
