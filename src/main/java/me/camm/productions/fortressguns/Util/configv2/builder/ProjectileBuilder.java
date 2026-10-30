package me.camm.productions.fortressguns.Util.configv2.builder;

import me.camm.productions.fortressguns.Util.configv2.Wrapper.Projectile.*;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;



public class ProjectileBuilder {
    
    static class FlakBuilder implements SectionBuilder<SectionFlak> {
        
        @Override
        public SectionFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = SectionBuilder.getDoubleOrInt(result, ConfigField.EP.build(key));
            Double ed = SectionBuilder.getDoubleOrInt(result, ConfigField.ED.build(key));
            Double dam = SectionBuilder.getDoubleOrInt(result, ConfigField.DAM.build(key));
            return new SectionFlak(ep, ed, dam, key);

        }
    }

    static class HeatMissileBuilder implements SectionBuilder<SectionHeatMissile> {
        @Override
        public SectionHeatMissile build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = SectionBuilder.getDoubleOrInt(result, ConfigField.EP.build(key));
            Double ed = SectionBuilder.getDoubleOrInt(result, ConfigField.ED.build(key));
            Double orb = SectionBuilder.getDoubleOrInt(result, ConfigField.ORBIT.build(key));
            Double prox = SectionBuilder.getDoubleOrInt(result, ConfigField.PROX.build(key));
            Long fuel = result.getLong(ConfigField.FUEL.build(key));
            Double maxSpd = SectionBuilder.getDoubleOrInt(result, ConfigField.MAX_SPD.build(key));
            Double acc = SectionBuilder.getDoubleOrInt(result, ConfigField.ACC.build(key));
            Double maxAcc = SectionBuilder.getDoubleOrInt(result, ConfigField.MAX_ACC.build(key));
            return new SectionHeatMissile(ep,ed,orb,prox,fuel, maxSpd, acc,maxAcc, key);
        }
    }


    static class HMGBulletBuilder implements SectionBuilder<SectionHMGBullet> {

        @Override
        public SectionHMGBullet build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = SectionBuilder.getDoubleOrInt(result, ConfigField.WEIGHT.build(key));
            Double dam = SectionBuilder.getDoubleOrInt(result, ConfigField.DAM.build(key));
            return new SectionHMGBullet(weight, dam, key);
        }
    }



    static class LightFlakBulletBuilder implements SectionBuilder<SectionLightFlakBullet> {

        @Override
        public SectionLightFlakBullet build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = SectionBuilder.getDoubleOrInt(result, ConfigField.WEIGHT.build(key));
            Double dam = SectionBuilder.getDoubleOrInt(result, ConfigField.DAM.build(key));
            return new SectionLightFlakBullet(weight, dam, key);
        }
    }


    static class ShellHeBuilder implements SectionBuilder<SectionShellHE> {

        @Override
        public SectionShellHE build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = SectionBuilder.getDoubleOrInt(result, ConfigField.EP.build(key));
            Double ed = SectionBuilder.getDoubleOrInt(result, ConfigField.ED.build(key));
            Double dam = SectionBuilder.getDoubleOrInt(result, ConfigField.DAM.build(key));
            return new SectionShellHE(ep, ed, dam, key);

        }
    }

    static class ShellSolidBuilder implements SectionBuilder<SectionShellSolid> {

        @Override
        public SectionShellSolid build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = SectionBuilder.getDoubleOrInt(result, ConfigField.WEIGHT.build(key));
            Double dam = SectionBuilder.getDoubleOrInt(result, ConfigField.DAM.build(key));
            return new SectionShellSolid(weight, dam, key);

        }
    }
    
    
}
