package me.camm.productions.fortressguns.ArtilleryItems;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFactory;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public enum AmmoItem {

    STANDARD_HEAVY(Material.LEVER, ChatColor.GRAY+"Solid Shell", new ProjectileFactory.FactoryStandardHeavy()),
    EXPLOSIVE_HEAVY(Material.LEVER, ChatColor.GRAY+"High Explosive Shell", new ProjectileFactory.FactoryExplosiveHeavy()),
    FLAK_HEAVY(Material.LEVER, ChatColor.GRAY+"Flak Shell", new ProjectileFactory.FactoryFlakHeavy()),
    STANDARD_LIGHT(Material.RAIL,ChatColor.GRAY+"Heavy Caliber Rounds", new ProjectileFactory.FactoryStandardLight()),
    FLAK_LIGHT(Material.RAIL, ChatColor.GRAY+"Light Flak Rounds", new ProjectileFactory.FactoryFlakLight()),
    MISSILE(Material.LEVER,ChatColor.GRAY + "Rocket", new ProjectileFactory.FactoryMissile()),
    CRAM(Material.RAIL, ChatColor.GRAY+"CRAM Explosive Rounds", new ProjectileFactory.FactoryCRAM());


    AmmoItem(Material mat, String name, ProjectileFactory<? extends ProjectileFG> factory) {
        this.mat = mat;
        this.name = name;
        this.factory = factory;
    }

    private final Material mat;
    private final  String name;
    private final ProjectileFactory<? extends ProjectileFG> factory;

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public ProjectileFG create(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        return factory.create(world, x,y,z, shooter, source);
    }

}
