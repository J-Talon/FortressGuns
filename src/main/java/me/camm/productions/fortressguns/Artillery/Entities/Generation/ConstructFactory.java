package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import me.camm.productions.fortressguns.ArtilleryItems.AmmoItem;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static me.camm.productions.fortressguns.Artillery.Entities.Generation.FactorySerialization.*;

public abstract class ConstructFactory<T extends Construct> {



    /*
    if you do make this version independent you have 2 choices:
    - either make a factory for each version or
    - make each function work out the version and return the correct version type < this will probably the thing we do
     */






    public abstract @Nullable T create(Location loc, int ... params);

    static final int ROTATION_MIN = 3;

    static class FactoryHeavyArtillery extends ConstructFactory<HeavyArtillery> {

        public HeavyArtillery create(Location loc, int... params) {

            if (params.length < ROTATION_MIN)
                return null;

            HeavyArtillery artillery = new HeavyArtillery(loc, loc.getWorld(),deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }


    static class FactoryHeavyFlak extends ConstructFactory<HeavyFlak> {

        public HeavyFlak create(Location loc, int... params) {
            if (params.length < ROTATION_MIN)
                return null;

            HeavyFlak artillery = new HeavyFlak(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }

    static class FactoryHMG extends ConstructFactory<HeavyMachineGun> {
        public HeavyMachineGun create(Location loc, int... params) {
            if (params.length < ROTATION_MIN) {
                return null;
            }

            HeavyMachineGun artillery = new HeavyMachineGun(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }

    static class FactoryLightFlak extends ConstructFactory<LightFlak> {
        public LightFlak create(Location loc, int... params) {
            if (params.length < ROTATION_MIN) {
                return null;
            }

            LightFlak artillery = new LightFlak(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }

    static class FactoryMissileLauncher extends ConstructFactory<MissileLauncher> {
        public MissileLauncher create(Location loc, int... params) {
            if (params.length < ROTATION_MIN) {
                return null;
            }

            MissileLauncher artillery = new MissileLauncher(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }

    static class FactoryLightArtillery extends ConstructFactory<LightArtillery> {
        public LightArtillery create(Location loc, int... params) {
            if (params.length < ROTATION_MIN) {
                return null;
            }

            LightArtillery artillery = new LightArtillery(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(artillery, params);
            deserializeSetHealth(artillery,params);
            return artillery;
        }
    }



}
