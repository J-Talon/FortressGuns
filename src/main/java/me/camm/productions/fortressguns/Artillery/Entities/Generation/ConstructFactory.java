package me.camm.productions.fortressguns.Artillery.Entities.Generation;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.MultiEntityGuns.*;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import static me.camm.productions.fortressguns.Util.Serialization.FactorySerialization.*;

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


    static class FactoryCRAM extends ConstructFactory<CRAM> {

        public CRAM create(Location loc, int ... params) {

            if (params.length < ROTATION_MIN) {
                return null;
            }


            CRAM cram = new CRAM(loc, loc.getWorld(), deserializeRotation(params));
            deserializeSetAmmo(cram, params);
            deserializeSetHealth(cram, params);
            return cram;

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


    static class FactoryTest extends ConstructFactory<TestGun> {

        public TestGun create(Location loc, int ... params) {
            return new TestGun(loc, loc.getWorld(), new EulerAngle(0,0,0));
        }
    }



}
