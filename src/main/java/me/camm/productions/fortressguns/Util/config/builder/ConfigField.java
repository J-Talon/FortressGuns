package me.camm.productions.fortressguns.Util.config.builder;

public enum ConfigField {

    WEIGHT("weight"),  //
    DAM("damage"),  //
    EP("explosionRadius"),  //
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
    ANGLE("angle"),
    PEN("penetrationPower"),

    HEALTH("health"),   //
    CD("cooldown"),   //
    MAG("magSize"),  //
    INF("infiniteAmmo"),   //
    JAM("jamPercent"),  //
    HEAT_IN("overheat"),  //
    HEAT_OUT("heatDissipationRate"),  //
    HEAT_TICKS("inactiveHeatTicks");  //


    private final String value;
    private ConfigField(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public String build(String in) {
        return in + "." + this;
    }
}