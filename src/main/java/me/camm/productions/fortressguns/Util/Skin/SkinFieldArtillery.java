package me.camm.productions.fortressguns.Util.Skin;

import static me.camm.productions.fortressguns.Util.Skin.ConstructSkin.TurretComponent.*;

public class SkinFieldArtillery extends ConstructSkin {

    public SkinFieldArtillery() {
        super();
        loadSkinOrDefault();
    }

    @Override
    protected void loadSkinOrDefault() {
        /*
        try loading the thing here from a file....?
        and if it doesn't work then load default.
         */

        //unfinished
        loadDefault();

    }

    @Override
    protected TurretComponent[] getTurretMappings() {
        return new TurretComponent[]{BODY, WHEEL, BASE, BARREL, SEAT};
    }

    @Override
    protected void loadDefault() {
        for (TurretComponent component: getTurretMappings()) {
            skinMap.put(component, component.getMat());
        }
    }

}
