package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

public enum Section {

    FIELD_HEAVY("fieldHeavy")



    private String section;
    private SectionBuilder<? extends ConfigSection> builder;

}
