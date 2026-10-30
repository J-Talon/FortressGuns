package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.*;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;


public class ArtilleryBuilder {
    

    static class FieldHeavyBuilder implements SectionBuilder<SectionFieldHeavy> {

        @Override
        public SectionFieldHeavy build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Long cd = result.getLong(ConfigHelper.Field.CD.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));
            return new SectionFieldHeavy(health, cd, inf, key);
        }
    }



    static class FieldLightBuilder implements SectionBuilder<SectionFieldLight> {

        @Override
        public SectionFieldLight build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Long cd = result.getLong(ConfigHelper.Field.CD.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));
            return new SectionFieldLight(health, cd, inf, key);
        }
    }


    static class HeavyFlakBuilder implements SectionBuilder<SectionHeavyFlak> {

        @Override
        public SectionHeavyFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Long cd = result.getLong(ConfigHelper.Field.CD.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));
            return new SectionHeavyFlak(health, cd, inf, key);
        }
    }

    static class HMGBuilder implements SectionBuilder<SectionHMG> {

        @Override
        public SectionHMG build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));

            Long mag = result.getLong(ConfigHelper.Field.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigHelper.Field.MAG.toString());
            int iMag = mag.intValue();

            Double jam = result.getDouble(ConfigHelper.Field.JAM.build(key));
            Double heatIn = result.getDouble(ConfigHelper.Field.HEAT_IN.build(key));
            Double heatOut = result.getDouble(ConfigHelper.Field.HEAT_OUT.build(key));

            Long ticks = result.getLong(ConfigHelper.Field.HEAT_TICKS.build(key));

            return new SectionHMG(health, iMag,jam, heatIn, heatOut, ticks, inf, key);

        }
    }


    static class LightFlakBuilder implements SectionBuilder<SectionLightFlak> {
        @Override
        public SectionLightFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));
            Long cd = result.getLong(ConfigHelper.Field.CD.build(key));

            Long mag = result.getLong(ConfigHelper.Field.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigHelper.Field.MAG.toString());
            int iMag = mag.intValue();

            Double jam = result.getDouble(ConfigHelper.Field.JAM.build(key));
            Double heatIn = result.getDouble(ConfigHelper.Field.HEAT_IN.build(key));
            Double heatOut = result.getDouble(ConfigHelper.Field.HEAT_OUT.build(key));

            Long ticks = result.getLong(ConfigHelper.Field.HEAT_TICKS.build(key));

            return new SectionLightFlak(health, cd, iMag, jam, heatIn, heatOut, ticks, inf, key);
        }
    }


    static class MissileLauncherBuilder implements SectionBuilder<SectionMissileLauncher> {

        @Override
        public SectionMissileLauncher build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = result.getDouble(ConfigHelper.Field.HEALTH.build(key));
            Long cd = result.getLong(ConfigHelper.Field.CD.build(key));
            Boolean inf = result.getBoolean(ConfigHelper.Field.INF.build(key));
            Long mag = result.getLong(ConfigHelper.Field.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigHelper.Field.MAG.toString());
            int iMag = mag.intValue();
            return new SectionMissileLauncher(health, cd, iMag, inf, key);
        }
    }



}
