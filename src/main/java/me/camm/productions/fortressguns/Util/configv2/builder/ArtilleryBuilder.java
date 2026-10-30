package me.camm.productions.fortressguns.Util.configv2.builder;

import me.camm.productions.fortressguns.Util.configv2.Wrapper.artillery.*;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;


public class ArtilleryBuilder {
    
    
    static class FieldHeavyBuilder implements SectionBuilder<SectionFieldHeavy> {

        @Override
        public SectionFieldHeavy build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Long cd = result.getLong(ConfigField.CD.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));
            return new SectionFieldHeavy(health, cd, inf, key);
        }
    }



    static class FieldLightBuilder implements SectionBuilder<SectionFieldLight> {

        @Override
        public SectionFieldLight build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Long cd = result.getLong(ConfigField.CD.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));
            return new SectionFieldLight(health, cd, inf, key);
        }
    }


    static class HeavyFlakBuilder implements SectionBuilder<SectionHeavyFlak> {

        @Override
        public SectionHeavyFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Long cd = result.getLong(ConfigField.CD.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));
            return new SectionHeavyFlak(health, cd, inf, key);
        }
    }

    static class HMGBuilder implements SectionBuilder<SectionHMG> {

        @Override
        public SectionHMG build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));

            Long mag = result.getLong(ConfigField.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigField.MAG.toString());
            int iMag = mag.intValue();

            Double jam = SectionBuilder.getDoubleOrInt(result, ConfigField.JAM.build(key));
            Double heatIn = SectionBuilder.getDoubleOrInt(result, ConfigField.HEAT_IN.build(key));
            Double heatOut = SectionBuilder.getDoubleOrInt(result, ConfigField.HEAT_OUT.build(key));

            Long ticks = result.getLong(ConfigField.HEAT_TICKS.build(key));

            return new SectionHMG(health, iMag,jam, heatIn, heatOut, ticks, inf, key);

        }
    }


    static class LightFlakBuilder implements SectionBuilder<SectionLightFlak> {
        @Override
        public SectionLightFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));
            Long cd = result.getLong(ConfigField.CD.build(key));

            Long mag = result.getLong(ConfigField.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigField.MAG.toString());
            int iMag = mag.intValue();

            Double jam = SectionBuilder.getDoubleOrInt(result, ConfigField.JAM.build(key));
            Double heatIn = SectionBuilder.getDoubleOrInt(result, ConfigField.HEAT_IN.build(key));
            Double heatOut = SectionBuilder.getDoubleOrInt(result, ConfigField.HEAT_OUT.build(key));

            Long ticks = result.getLong(ConfigField.HEAT_TICKS.build(key));

            return new SectionLightFlak(health, cd, iMag, jam, heatIn, heatOut, ticks, inf, key);
        }
    }


    static class MissileLauncherBuilder implements SectionBuilder<SectionMissileLauncher> {

        @Override
        public SectionMissileLauncher build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double health = SectionBuilder.getDoubleOrInt(result, ConfigField.HEALTH.build(key));
            Long cd = result.getLong(ConfigField.CD.build(key));
            Boolean inf = result.getBoolean(ConfigField.INF.build(key));
            Long mag = result.getLong(ConfigField.MAG.build(key));
            if (mag == null) throw new NullPointerException(ConfigField.MAG.toString());
            int iMag = mag.intValue();
            return new SectionMissileLauncher(health, cd, iMag, inf, key);
        }
    }



}
