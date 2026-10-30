package me.camm.productions.fortressguns.Util.Config.v2.builder;

import me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Etc.SectionGeneral;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;

public class EtcBuilder {

    /*
    #whether flares should be enabled to help players survive barrages of missiles
enableFlares = true

#Whether the explosions from the artillery should be
#vanilla explosions which call Spigot/Bukkit/Paper API events using world.createExplosion(...)
#or a custom explosion which might not
useVanillaExplosions = false

#Whether artillery destroys blocks
#this does not apply to falling blocks
destructiveExplosions = true

     */
    
    
    static class GeneralConfigBuilder implements SectionBuilder<SectionGeneral> {

        @Override
        public SectionGeneral build(TomlParseResult result, String key) throws IllegalArgumentException, NullPointerException, TomlInvalidTypeException {
            Boolean flare = result.getBoolean(ConfigHelper.Field.FLARES.build(key));
            Boolean vanilla = result.getBoolean(ConfigHelper.Field.VANILLA.build(key));
            Boolean dest = result.getBoolean(ConfigHelper.Field.DESTRUCTIVE.build(key));
            return new SectionGeneral(flare, vanilla, dest);
        }
    }

}
