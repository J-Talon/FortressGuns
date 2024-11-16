package me.camm.productions.fortressguns.Util.DataLoading.Schema;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;

public enum ArtillerySchema {

    FIELD_LIGHT(ArtilleryType.FIELD_LIGHT,new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN}),
    FIELD_HEAVY(ArtilleryType.FIELD_HEAVY, new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN}),

    HEAVY_FLAK(ArtilleryType.FLAK_HEAVY, new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN}),
    LIGHT_FLAK(ArtilleryType.FLAK_LIGHT,new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN, SchemaKey.MAG_SIZE,SchemaKey.JAM_PERCENT, SchemaKey.OVERHEAT}),

    HEAVY_MACHINE_GUN(ArtilleryType.HEAVY_MACHINE, new SchemaKey[]{SchemaKey.HEALTH,SchemaKey.MAG_SIZE,SchemaKey.JAM_PERCENT, SchemaKey.OVERHEAT}),
    MISSILE_LAUNCHER(ArtilleryType.MISSILE_LAUNCHER,new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN, SchemaKey.NUM_MISSILES}),

    RAIL_GUN(ArtilleryType.RAIL_GUN, new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN, SchemaKey.RANGE, SchemaKey.RANGE}),
    CRAM(ArtilleryType.CRAM,new SchemaKey[]{SchemaKey.HEALTH, SchemaKey.COOLDOWN});




    private final ArtilleryType type;
    private final SchemaKey[] configValues;

    private ArtillerySchema(ArtilleryType type, SchemaKey[] configValues) {
        this.type = type;
        this.configValues = configValues;
    }

    public ArtilleryType getType() {
        return type;
    }

    public SchemaKey[] getConfigValues() {
        return configValues;
    }
}
