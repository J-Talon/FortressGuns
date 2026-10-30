package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Projectile.*;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;



public class ProjectileBuilder {
    
    static class FlakBuilder implements SectionBuilder<SectionFlak> {
        
        @Override
        public SectionFlak build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = result.getDouble(ConfigHelper.Field.EP.build(key));
            Double ed = result.getDouble(ConfigHelper.Field.ED.build(key));
            Double dam = result.getDouble(ConfigHelper.Field.DAM.build(key));
            return new SectionFlak(ep, ed, dam);

        }
    }

    static class HeatMissileBuilder implements SectionBuilder<SectionHeatMissile> {
        @Override
        public SectionHeatMissile build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = result.getDouble(ConfigHelper.Field.EP.build(key));
            Double ed = result.getDouble(ConfigHelper.Field.ED.build(key));
            Double orb = result.getDouble(ConfigHelper.Field.ORBIT.build(key));
            Double prox = result.getDouble(ConfigHelper.Field.PROX.build(key));
            Long fuel = result.getLong(ConfigHelper.Field.FUEL.build(key));
            Double maxSpd = result.getDouble(ConfigHelper.Field.MAX_SPD.build(key));
            Double acc = result.getDouble(ConfigHelper.Field.ACC.build(key));
            Double maxAcc = result.getDouble(ConfigHelper.Field.MAX_ACC.build(key));
            return new SectionHeatMissile(ep,ed,orb,prox,fuel, maxSpd, acc,maxAcc);
        }
    }


    static class HMGBulletBuilder implements SectionBuilder<SectionHMGBullet> {

        @Override
        public SectionHMGBullet build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = result.getDouble(ConfigHelper.Field.WEIGHT.build(key));
            Double dam = result.getDouble(ConfigHelper.Field.DAM.build(key));
            return new SectionHMGBullet(weight, dam);
        }
    }



    static class LightFlakBulletBuilder implements SectionBuilder<SectionLightFlakBullet> {

        @Override
        public SectionLightFlakBullet build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = result.getDouble(ConfigHelper.Field.WEIGHT.build(key));
            Double dam = result.getDouble(ConfigHelper.Field.DAM.build(key));
            return new SectionLightFlakBullet(weight, dam);
        }
    }


    static class ShellHeBuilder implements SectionBuilder<SectionShellHE> {

        @Override
        public SectionShellHE build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double ep = result.getDouble(ConfigHelper.Field.EP.build(key));
            Double ed = result.getDouble(ConfigHelper.Field.ED.build(key));
            Double dam = result.getDouble(ConfigHelper.Field.DAM.build(key));
            return new SectionShellHE(ep, ed, dam);

        }
    }

    static class ShellSolidBuilder implements SectionBuilder<SectionShellSolid> {

        @Override
        public SectionShellSolid build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Double weight = result.getDouble(ConfigHelper.Field.WEIGHT.build(key));
            Double dam = result.getDouble(ConfigHelper.Field.DAM.build(key));
            return new SectionShellSolid(weight, dam);

        }
    }
    
    
}
