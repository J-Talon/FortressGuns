package me.camm.productions.fortressguns.Util.Config.v2.Wrapper.Etc;


import me.camm.productions.fortressguns.Util.Config.v2.ConfigSection;

/*
[general]
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
public class SectionGeneral extends ConfigSection {

    private Boolean enableFlares;
    private Boolean useVanillaExplosions;
    private Boolean destructiveExplosions;


    public SectionGeneral(Boolean enableFlares, Boolean useVanillaExplosions, Boolean destructiveExplosions) {
        this.enableFlares = enableFlares;
        this.useVanillaExplosions = useVanillaExplosions;
        this.destructiveExplosions = destructiveExplosions;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    public Boolean isEnableFlares() {
        return enableFlares;
    }

    public Boolean isUseVanillaExplosions() {
        return useVanillaExplosions;
    }

    public Boolean getDestructiveExplosions() {
        return destructiveExplosions;
    }
}
