package me.camm.productions.fortressguns.Util.DataLoading.Config;


import com.fasterxml.jackson.annotation.JsonTypeName;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.FlakHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShellHE;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.StandardHeavyShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.FlakLightShell;
import me.camm.productions.fortressguns.Artillery.Projectiles.LightShell.StandardLightShell;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("contactDamage")
public class ConfigArtilleryContact implements ConfigObject {

    protected float heavyHighExplosive = 10;
    protected float heavyFlak = 10;
    protected float solid = 25;
    protected float lightSolid = 5;
    protected float lightFlak = 5;
    protected float cram = 10;
    protected float railgun = 150;


    @Override
    public boolean apply() {
        ValidatorContact contact = new ValidatorContact();
        if (!contact.validate(this))
                return false;

        HeavyShellHE.setHitDamage(heavyHighExplosive);
        FlakHeavyShell.setHitDamage(heavyFlak);
        StandardHeavyShell.setHitDamage(solid);

        StandardLightShell.setHitDamage(lightSolid);
        FlakLightShell.setHitDamage(lightFlak);


        //   CRAMShell.setHitDamage(cram);
        //   railgun

        return true;
    }

    static class ValidatorContact implements Validator<ConfigArtilleryContact> {
        @Override
        public boolean validate(@NotNull ConfigArtilleryContact in) {
            return in.heavyHighExplosive >= 0 &&
                    in.heavyFlak >= 0 &&
                    in.solid >= 0 &&
                    in.lightSolid >= 0 &&
                    in.lightFlak >= 0 &&
                    in.cram >= 0 &&
                    in.railgun >= 0;
        }
    }




    public float getHeavyHighExplosive() {
        return heavyHighExplosive;
    }

    public void setHeavyHighExplosive(float heavyHighExplosive) {
        this.heavyHighExplosive = heavyHighExplosive;
    }

    public float getHeavyFlak() {
        return heavyFlak;
    }

    public void setHeavyFlak(float heavyFlak) {
        this.heavyFlak = heavyFlak;
    }

    public float getSolid() {
        return solid;
    }

    public void setSolid(float solid) {
        this.solid = solid;
    }

    public float getLightSolid() {
        return lightSolid;
    }

    public void setLightSolid(float lightSolid) {
        this.lightSolid = lightSolid;
    }

    public float getLightFlak() {
        return lightFlak;
    }

    public void setLightFlak(float lightFlak) {
        this.lightFlak = lightFlak;
    }

    public float getCram() {
        return cram;
    }

    public void setCram(float cram) {
        this.cram = cram;
    }

    public float getRailgun() {
        return railgun;
    }

    public void setRailgun(float railgun) {
        this.railgun = railgun;
    }
}
