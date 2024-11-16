package me.camm.productions.fortressguns.Util.DataLoading.Schema;

public enum SchemaKey {

    HEALTH("health", Integer.class),
    COOLDOWN("cooldown", Integer.class),
    MAG_SIZE("magsize", Integer.class),
    JAM_PERCENT("jampercent", Float.class),
    OVERHEAT("overheat", Float.class),
    RANGE("range", Integer.class),
    NUM_MISSILES("missiles", Integer.class),

    MAX_DAMAGE("maxdamage", Float.class);

    private final Class<?> clazz;
    private final String label;

    private SchemaKey(String label, Class<?> clazz) {
        this.label = label;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getLabel() {
        return label;
    }
}
