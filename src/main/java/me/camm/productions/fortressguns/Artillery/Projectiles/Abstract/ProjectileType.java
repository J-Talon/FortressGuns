package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;


import org.bukkit.entity.Entity;

public interface ProjectileType {

    public interface ISolidHeavyShell extends ProjectileFG {}

    public interface IHeavyShellHE extends ProjectileFG {}

    public interface IFlakHeavyShell extends ProjectileFG {

        void setExplodeTime(double time);

        void setTerminus(Entity target);



    }



    public interface ICRAMShell extends ProjectileFG {}

    public interface ISolidLightShell extends ProjectileFG {}

    public interface IFlakLightShell extends ProjectileFG {}


    public interface ISimpleFlare extends ProjectileFG {}

    public interface IHeatSeekingRocket extends ProjectileFG {}


}


