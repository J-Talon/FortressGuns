package me.camm.productions.fortressguns.Util.Config.v2;

import java.util.logging.Logger;

public abstract class ConfigSection {

    protected String key;
    public ConfigSection(String key) {
        this.key = key;
    }

    public abstract boolean isValid(Logger logger);


    protected void log(String variable, String key, Logger logger) {
        logger.warning("Parameter " +variable +" in section  "+key+" failed validation");
    }

}
