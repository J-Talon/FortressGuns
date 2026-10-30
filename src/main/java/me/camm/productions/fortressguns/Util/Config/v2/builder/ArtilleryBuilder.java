package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;
import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.artillery.*;
import org.tomlj.TomlParseResult;

public class ArtilleryBuilder {


    static class FieldHeavyBuilder implements SectionBuilder<SectionFieldHeavy> {

        @Override
        public SectionFieldHeavy build(TomlParseResult result) throws IllegalArgumentException {
            return null;
        }
    }



    static class FieldLightBuilder implements SectionBuilder<SectionFieldLight> {

        @Override
        public SectionFieldLight build(TomlParseResult result) throws IllegalArgumentException {
            return null;
        }
    }


    static class HeavyFlakBuilder implements SectionBuilder<SectionHeavyFlak> {

        @Override
        public SectionHeavyFlak build(TomlParseResult result) throws IllegalArgumentException {
            return null;
        }
    }


    static class HMGBuilder implements SectionBuilder<SectionHMG> {

        @Override
        public SectionHMG build(TomlParseResult result) throws IllegalArgumentException {
            return null;
        }
    }


    static class LightFlakBuilder  implements SectionBuilder<SectionLightFlak> {


        @Override
        public SectionLightFlak build(TomlParseResult result) throws IllegalArgumentException {
            return null;
        }
    }



}
