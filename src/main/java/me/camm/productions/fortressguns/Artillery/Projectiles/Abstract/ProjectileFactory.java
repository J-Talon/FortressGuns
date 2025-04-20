package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShellHE;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.CRAMShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.World;
import org.jetbrains.annotations.Nullable;


@FunctionalInterface
public interface ProjectileFactory<T extends ArtilleryProjectile> {

    public abstract T create(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Artillery source);



    public class FactoryStandardHeavy implements ProjectileFactory<StandardHeavyShell> {
        @Override
        public StandardHeavyShell create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new StandardHeavyShell(world, x, y, z, shooter, source);
        }
    }


    public class FactoryStandardLight implements ProjectileFactory<StandardLightShell> {
        @Override
        public StandardLightShell create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new StandardLightShell(world, x, y, z, shooter, source);
        }
    }

    public class FactoryExplosiveHeavy implements ProjectileFactory<HeavyShellHE> {
        @Override
        public HeavyShellHE create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new HeavyShellHE(world, x, y, z, shooter, source);
        }
    }


    public class FactoryFlakLight implements ProjectileFactory<FlakLightShell> {
        @Override
        public FlakLightShell create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new FlakLightShell(world, x, y, z, shooter, source);
        }
    }

    public class FactoryFlakHeavy implements ProjectileFactory<FlakHeavyShell> {
        @Override
        public FlakHeavyShell create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new FlakHeavyShell(world, x, y, z, shooter, source);
        }
    }


    public class FactoryMissile implements ProjectileFactory<SimpleMissile> {
        @Override
        public SimpleMissile create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new SimpleMissile(world, x, y, z, shooter, source);
        }
    }

    public class FactoryCRAM implements ProjectileFactory<CRAMShell> {
        @Override
        public CRAMShell create(World world, double x, double y, double z, EntityPlayer shooter, Artillery source) {
            return new CRAMShell(world, x, y, z, shooter, source);
        }

    }

}

