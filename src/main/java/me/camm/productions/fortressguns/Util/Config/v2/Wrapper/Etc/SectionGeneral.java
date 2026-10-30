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

    private boolean enableFlares;
    private boolean useVanillaExplosions;
    private double destructiveExplosions;


    public SectionGeneral(boolean enableFlares, boolean useVanillaExplosions, double destructiveExplosions) {
        this.enableFlares = enableFlares;
        this.useVanillaExplosions = useVanillaExplosions;
        this.destructiveExplosions = destructiveExplosions;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    public boolean isEnableFlares() {
        return enableFlares;
    }

    public boolean isUseVanillaExplosions() {
        return useVanillaExplosions;
    }

    public double getDestructiveExplosions() {
        return destructiveExplosions;
    }
}
