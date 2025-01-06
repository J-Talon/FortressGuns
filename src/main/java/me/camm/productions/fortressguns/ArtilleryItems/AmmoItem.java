package me.camm.productions.fortressguns.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.ExplosiveHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.CRAMShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum AmmoItem {

    STANDARD_HEAVY(Material.LEVER, ChatColor.GRAY+"Standard Shell", StandardHeavyShell.class),
    EXPLOSIVE_HEAVY(Material.LEVER, ChatColor.GRAY+"High Explosive Shell", ExplosiveHeavyShell.class),
    FLAK_HEAVY(Material.LEVER, ChatColor.GRAY+"Flak Shell", FlakHeavyShell.class),
    STANDARD_LIGHT(Material.RAIL,ChatColor.GRAY+"Heavy Caliber Rounds", StandardLightShell.class),
    FLAK_LIGHT(Material.RAIL, ChatColor.GRAY+"Light Flak Rounds", FlakLightShell.class),
    MISSILE(Material.LEVER,ChatColor.GRAY + "Rocket", SimpleMissile.class),
    CRAM(Material.RAIL, ChatColor.GRAY+"CRAM Explosive Rounds", CRAMShell.class);


    private AmmoItem(Material mat, String name, Class<? extends ArtilleryProjectile> projClass) {
        this.mat = mat;
        this.name = name;
        this.projClass = projClass;
    }

    private final Material mat;
    private final  String name;
    private final Class<? extends ArtilleryProjectile> projClass;

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public Class<? extends ArtilleryProjectile> getProjClass() {
        return projClass;
    }
}
