package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFactory;
import me.camm.productions.fortressguns.interact.item.classification.FGItems;
import me.camm.productions.fortressguns.interact.item.classification.FGSingleConsumable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public enum AmmoItem {

    STANDARD_HEAVY(FGItems.SOLID_SHELL, new ProjectileFactory.FactoryStandardHeavy()),
    EXPLOSIVE_HEAVY(FGItems.HE_SHELL, new ProjectileFactory.FactoryExplosiveHeavy()),
    FLAK_HEAVY(FGItems.FLAK_SHELL, new ProjectileFactory.FactoryFlakHeavy()),
    STANDARD_LIGHT(FGItems.HMG_BULLET, new ProjectileFactory.FactoryStandardLight()),
    FLAK_LIGHT(FGItems.LIGHT_FLAK_BULLET, new ProjectileFactory.FactoryFlakLight()),
    MISSILE(FGItems.HEAT_SEEKER_MISSILE, new ProjectileFactory.FactoryMissile()),
    CRAM(FGItems.CRAM_BULLET, new ProjectileFactory.FactoryCRAM()),
    FLARE(FGItems.FLARE, new ProjectileFactory.FactoryFlare());


    AmmoItem(FGSingleConsumable cons, ProjectileFactory<? extends ProjectileFG> factory) {
        this.item = cons;
        this.factory = factory;
    }

    private final FGSingleConsumable item;
    private final ProjectileFactory<? extends ProjectileFG> factory;

    public Material getMat() {
        return item == null ? null : item.getMaterial();
    }

    public String getName() {
        return item == null ? null : item.getDisplayName();
    }

    public ProjectileFG create(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source) {
        return factory.create(world, x,y,z, shooter, source);
    }

}
