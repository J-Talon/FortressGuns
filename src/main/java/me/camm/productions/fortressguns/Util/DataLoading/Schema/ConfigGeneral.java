package me.camm.productions.fortressguns.Util.DataLoading.Schema;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShellHE;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.CRAMShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import me.camm.productions.fortressguns.Util.DataLoading.Validator.ValidatorGeneral;
import me.camm.productions.fortressguns.Explosion.Old.ExplosionFactory;

@JsonTypeName("general")
public class ConfigGeneral implements ConfigObject {


    //yields
    double heavyHighExplosive; //  done
    double heavyFlak;  //  done
    double missile;  //  done


    double standard; // done

    //damage
    double heavyHighExplosiveDamage;  // done
    double heavyFlakDamage; //  done
    double lightStandardDamage;  //  done
    double lightFlakDamage;  //  done
    double cramDamage;  // done
    double railgunDamage;   //not implemented yet

    double standardDamage;  // done

    boolean useVanillaExplosions;   // done
    boolean destructiveArtillery;   // done


    boolean requireReloading;   //  not implemented yet


    boolean enableFlares;  //not implemented yet
    double missileDifficulty;   // not implemented yet



    @Override
    public boolean apply() {
        ValidatorGeneral general = new ValidatorGeneral();
        if (!general.validate(this)) {
            return false;
        }

        HeavyShellHE.setExplosionPower((float)heavyHighExplosive);
        HeavyShellHE.setHitDamage((float)heavyHighExplosiveDamage);

        FlakHeavyShell.setExplosionPower((float)heavyFlak);
        FlakHeavyShell.setHitDamage((float)heavyFlakDamage);

        SimpleMissile.setExplosionPower((float)missile);

        StandardHeavyShell.setExplosionPower((float)standard);
        StandardHeavyShell.setHitDamage((float)standardDamage);

        StandardLightShell.setHitDamage((float)lightStandardDamage);

        FlakLightShell.setHitDamage((float)lightFlakDamage);

        CRAMShell.setHitDamage((float)cramDamage);


        Artillery.setRequiresReloading(requireReloading);




        ExplosionFactory.setDestructiveExplosions(destructiveArtillery);
        ExplosionFactory.setUseVanillaExplosions(useVanillaExplosions);





        return true;
    }



    public double[] getDoubleValues() {
        return new double[]{
                heavyFlak,missile,heavyHighExplosive, standard,

                heavyHighExplosiveDamage, heavyFlakDamage, lightStandardDamage,
                lightFlakDamage, cramDamage, railgunDamage, standardDamage
        };
    }


    public double getStandard() {
        return standard;
    }

    public void setStandard(double standard) {
        this.standard = standard;
    }

    public double getStandardDamage() {
        return standardDamage;
    }

    public void setStandardDamage(double standardDamage) {
        this.standardDamage = standardDamage;
    }

    public double getHeavyHighExplosive() {
        return heavyHighExplosive;
    }

    public void setHeavyHighExplosive(double heavyHighExplosive) {
        this.heavyHighExplosive = heavyHighExplosive;
    }

    public double getHeavyFlak() {
        return heavyFlak;
    }

    public void setHeavyFlak(double heavyFlak) {
        this.heavyFlak = heavyFlak;
    }

    public double getMissile() {
        return missile;
    }

    public void setMissile(double missile) {
        this.missile = missile;
    }

    public double getHeavyHighExplosiveDamage() {
        return heavyHighExplosiveDamage;
    }

    public void setHeavyHighExplosiveDamage(double heavyHighExplosiveDamage) {
        this.heavyHighExplosiveDamage = heavyHighExplosiveDamage;
    }

    public double getHeavyFlakDamage() {
        return heavyFlakDamage;
    }

    public void setHeavyFlakDamage(double heavyFlakDamage) {
        this.heavyFlakDamage = heavyFlakDamage;
    }

    public double getLightStandardDamage() {
        return lightStandardDamage;
    }

    public void setLightStandardDamage(double lightStandardDamage) {
        this.lightStandardDamage = lightStandardDamage;
    }

    public double getLightFlakDamage() {
        return lightFlakDamage;
    }

    public void setLightFlakDamage(double lightFlakDamage) {
        this.lightFlakDamage = lightFlakDamage;
    }

    public double getCramDamage() {
        return cramDamage;
    }

    public void setCramDamage(double cramDamage) {
        this.cramDamage = cramDamage;
    }

    public double getRailgunDamage() {
        return railgunDamage;
    }

    public void setRailgunDamage(double railgunDamage) {
        this.railgunDamage = railgunDamage;
    }

    public boolean isUseVanillaExplosions() {
        return useVanillaExplosions;
    }

    public void setUseVanillaExplosions(boolean useVanillaExplosions) {
        this.useVanillaExplosions = useVanillaExplosions;
    }

    public boolean isDestructiveArtillery() {
        return destructiveArtillery;
    }

    public void setDestructiveArtillery(boolean destructiveArtillery) {
        this.destructiveArtillery = destructiveArtillery;
    }

    public boolean isRequireReloading() {
        return requireReloading;
    }

    public void setRequireReloading(boolean requireReloading) {
        this.requireReloading = requireReloading;
    }

    public boolean isEnableFlares() {
        return enableFlares;
    }

    public void setEnableFlares(boolean enableFlares) {
        this.enableFlares = enableFlares;
    }

    public double getMissileDifficulty() {
        return missileDifficulty;
    }

    public void setMissileDifficulty(double missileDifficulty) {
        this.missileDifficulty = missileDifficulty;
    }
}
