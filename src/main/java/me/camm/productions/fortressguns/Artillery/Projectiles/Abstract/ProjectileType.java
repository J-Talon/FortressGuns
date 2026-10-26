package me.camm.productions.fortressguns.Artillery.Projectiles.Abstract;

public interface ProjectileType {

    public interface ISolidHeavyShell extends ProjectileFG {}

    public interface IHeavyShellHE extends ProjectileFG {}

    public interface IFlakHeavyShell extends ProjectileFG {}



    public interface ICRAMShell extends ProjectileFG {}

    public interface ISolidLightShell extends ProjectileFG {}

    public interface IFlakLightShell extends ProjectileFG {}


    public interface ISimpleFlare extends ProjectileFG {}

    public interface IHeatSeekingRocket extends ProjectileFG {}


}


