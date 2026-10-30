package me.camm.productions.fortressguns.Util.Config.v2.builder;

public class ConfigHelper {

    public static enum Field {

        WEIGHT("weight"),  //
        DAM("damage"),  //
        EP("explosionPower"),  //
        ED("explosionDamage"),  //
        ORBIT("orbitDist"),
        PROX("explosionProximity"),
        FUEL("fuelTicks"),
        MAX_SPD("maxSpeed"),
        ACC("acceleration"),
        MAX_ACC("maxAcceleration"),

        FLARES("enableFlares"),
        VANILLA("useVanillaExplosions"),
        DESTRUCTIVE("destructiveExplosions"),

        HEALTH("health"),   //
        CD("cooldown"),   //
        MAG("magSize"),  //
        INF("infiniteAmmo"),   //
        JAM("jamPercent"),  //
        HEAT_IN("overheat"),  //
        HEAT_OUT("heatDissipationRate"),  //
        HEAT_TICKS("inactiveHeatTicks");  //


        private final String value;
        private Field(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String build(String in) {
            return in + "." + this;
        }
    }



    public static void checkHCDI(Double health, Long cd, Boolean inf) throws IllegalArgumentException {
        if (health == null || health < 0) throw new IllegalArgumentException(Field.HEALTH.toString());
        if (cd == null || cd < 0) throw new IllegalArgumentException(Field.CD.toString());
        if (inf == null) throw new IllegalArgumentException(Field.INF.toString());
    }

    public static void checkHeats(Double heatIn, Double heatOut, Long heatTicks) throws IllegalArgumentException {
        if (heatIn == null || heatIn < 0 || heatIn > 100) throw new IllegalArgumentException(Field.HEAT_IN.toString());
        if (heatOut == null || heatOut < 0 || heatOut > 100) throw new IllegalArgumentException(Field.HEAT_OUT.toString());
        if (heatTicks == null || heatTicks < 0) throw new IllegalArgumentException(Field.HEAT_TICKS.toString());
    }

    public static void checkHealth(Double health) {
        if (health == null || health < 0) throw new IllegalArgumentException(Field.HEALTH.toString());
    }

    public static void checkMag(Integer val) {
        if (val == null || val < 0) throw new IllegalArgumentException(Field.MAG.toString());
    }

    public static void checkCD(Long cd) {
        if (cd == null || cd < 0) throw new IllegalArgumentException(Field.CD.toString());
    }

    public static void checkMI(Integer val, Boolean inf) {
        if (val == null || val < 0) throw new IllegalArgumentException(Field.MAG.toString());
        if (inf == null) throw new IllegalArgumentException(Field.INF.toString());
    }

    public static void checkJam(Double val) {
        if (val == null || val < 0 || val > 100) throw new IllegalArgumentException(Field.JAM.toString());
    }

    public static void checkProjWD(Double weight, Double dam) {
        if (weight == null || weight < 0) throw new IllegalArgumentException(Field.WEIGHT.toString());
        if (dam == null || dam < 0) throw new IllegalArgumentException(Field.DAM.toString());
    }

    public static void checkProjExplodes3(Double expPow, Double expDam, Double dam) {
        if (dam == null || dam < 0) throw new IllegalArgumentException(Field.DAM.toString());
        if (expDam == null || expDam < 0) throw new IllegalArgumentException(Field.ED.toString());
        if (expPow == null || expPow < 0) throw new IllegalArgumentException(Field.EP.toString());
    }

    public static void checkProjExplodes2(Double expPow, Double expDam) {
        if (expDam == null || expDam < 0) throw new IllegalArgumentException(Field.ED.toString());
        if (expPow == null || expPow < 0) throw new IllegalArgumentException(Field.EP.toString());
    }


    //I should put this in an array or something huh?
    public static void checkMissile(Double orbit, Double prox, Long fuel, Double maxSpd, Double acc, Double maxAcc) {
        if (orbit == null || orbit < 0) throw new IllegalArgumentException(Field.ORBIT.toString());
        if (prox == null || prox < 0) throw new IllegalArgumentException(Field.PROX.toString());
        if (fuel == null || fuel < 0) throw new IllegalArgumentException(Field.FUEL.toString());
        if (maxSpd == null || maxSpd < 0) throw new IllegalArgumentException(Field.MAX_SPD.toString());
        if (acc == null || acc < 0) throw new IllegalArgumentException(Field.ACC.toString());
        if (maxAcc == null || maxAcc < 0) throw new IllegalArgumentException(Field.MAX_ACC.toString());

    }


    public static void checkGeneral(Boolean flares, Boolean vanilla, Boolean dest) {
        if (flares == null) throw new IllegalArgumentException(Field.FLARES.toString());
        if (vanilla == null) throw new IllegalArgumentException(Field.VANILLA.toString());
        if (dest == null) throw new IllegalArgumentException(Field.DESTRUCTIVE.toString());
    }

}
