package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShellHE;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.CRAMShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.HeatseekingMissile;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.camm.productions.fortressguns.Artillery.Projectiles.Flare.SimpleFlare;


@FunctionalInterface
public interface ProjectileFactory<T extends ProjectileFG> {

    @NotNull T create(World world, double x, double y, double z, @Nullable Player shooter, @Nullable Artillery source);

    default T create(World world, double x, double y, double z, Vector velocity, @Nullable Player shooter, @Nullable Artillery source) {
        T projectile = this.create(world, x,y,z, shooter, source);
        projectile.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
        return projectile;
    }


    /*


    Factory (not lambda) <-- put into enum
        inner factory (lambda)
            -> create proj


    Version v;

    class ... implements <> {

        2 ways:
         -- many if statements < try to avoid
         -- some way to do with setting it specifically < lambdas


        Lambda l;
        setVersion(Version v) {
            return <.. some lambda corresponding to version ..>
        }

    }

     */


    class FactoryStandardHeavy implements ProjectileFactory<ProjectileType.ISolidHeavyShell> {
        @Override
        public @NotNull ProjectileType.ISolidHeavyShell create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new StandardHeavyShell(world, x, y, z, shooter, source);
        }
    }


    class FactoryStandardLight implements ProjectileFactory<ProjectileType.ISolidLightShell> {
        @Override
        public @NotNull ProjectileType.ISolidLightShell create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new StandardLightShell(world, x, y, z, shooter, source);
        }
    }

    class FactoryExplosiveHeavy implements ProjectileFactory<ProjectileType.IHeavyShellHE> {
        @Override
        public @NotNull ProjectileType.IHeavyShellHE create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new HeavyShellHE(world, x, y, z, shooter, source);
        }
    }


    class FactoryFlakLight implements ProjectileFactory<ProjectileType.IFlakLightShell> {
        @Override
        public @NotNull ProjectileType.IFlakLightShell create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new FlakLightShell(world, x, y, z, shooter, source);
        }
    }

    class FactoryFlakHeavy implements ProjectileFactory<ProjectileType.IFlakHeavyShell> {
        @Override
        public @NotNull ProjectileType.IFlakHeavyShell create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new FlakHeavyShell(world, x, y, z, shooter, source);
        }
    }


    class FactoryMissile implements ProjectileFactory<ProjectileType.IHeatSeekingRocket> {
        @Override
        public @NotNull ProjectileType.IHeatSeekingRocket create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new HeatseekingMissile(world, x, y, z, shooter, source);
        }
    }

    class FactoryCRAM implements ProjectileFactory<ProjectileType.ICRAMShell> {
        @Override
        public @NotNull ProjectileType.ICRAMShell create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new CRAMShell(world, x, y, z, shooter, source);
        }

    }

    class FactoryFlare implements ProjectileFactory<ProjectileType.ISimpleFlare> {
        @Override
        public @NotNull ProjectileType.ISimpleFlare create(World world, double x, double y, double z, Player shooter, Artillery source) {
            return new SimpleFlare(world, x, y, z, shooter);
        }



    }

}

