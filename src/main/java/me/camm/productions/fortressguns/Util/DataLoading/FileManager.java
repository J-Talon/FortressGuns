package me.camm.productions.fortressguns.Util.DataLoading;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.DataLoading.Adapters.AdapterArtillery;
import me.camm.productions.fortressguns.Util.DataLoading.Adapters.AdapterBuilder;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.ArtillerySchema;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.SchemaKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.tomlj.Toml;
import org.tomlj.TomlInvalidTypeException;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    static Map<String, ArtilleryType> types = new HashMap<>();


    enum Resource {

        CONFIG("ArtilleryConfig.toml"),
        SAVES("SavedArtillery.toml"),
        SKINS("Skins.txt");
        private Resource(String file) {
            this.file = file;
        }

        public String get() {
            return file;
        }

        private final String file;
    }

    static {
        for (ArtilleryType t : ArtilleryType.values()) {
            types.put(t.getId(),t);
        }
    }

    public static String getFilePath(){
        return getFolderPath() +"\\";
    }

    public static String getFolderPath(){
        Plugin plugin = FortressGuns.getInstance();
        return plugin.getDataFolder().getParentFile().getAbsolutePath()+ "\\FortressGuns";
    }


    public static File initResource(Resource res) throws IOException {

        String name = res.get();
        InputStream stream = FortressGuns.getInstance().getResource(name);

        File folder = new File(getFolderPath());
        boolean made = true;

        if (!folder.exists()) {
            made = folder.mkdir();
            made = made && folder.createNewFile();
        }

        if (!made)
            throw new NoSuchFileException("Could not create plugin folder!");

        if (stream == null) {
            throw new NoSuchFileException("Artillery config not found!");
        }

        File file = new File(getFilePath()+name);
        if (file.exists())
            return file;

        Files.copy(stream,file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }



    public static boolean loadArtilleryConfig() {

        Map<ArtilleryType, Map<String, Object>> values = new HashMap<>();
        Plugin plugin = FortressGuns.getInstance();
        boolean errored = false;

        try {
            File file = initResource(Resource.CONFIG);
            Path path = Paths.get(file.getPath());
            TomlParseResult res = Toml.parse(path);

            if (res.hasErrors()) {
                plugin.getLogger().warning("Artillery config failed to load due to TOML errors in the file, trying to load defaults...");
                return tryLoadDefaults();
            }

            for (ArtillerySchema schema: ArtillerySchema.values()) {
                Map<String, Object> map = getValue(schema, res);
                if (map == null) {
                    errored = true;
                    values.put(schema.getType(),null);
                }
                else
                    values.put(schema.getType(), map);
            }
        }
        catch (IOException e) {
            plugin.getLogger().warning("Could not load config due to IO errors- Trying to load default config...");
            return tryLoadDefaults();
        }

        if (errored) {
            plugin.getLogger().warning("Some Artillery Config did not load due to parsing errors. Attempting to load default values for them.");
        }


            for (ArtilleryType type : values.keySet()) {

                Class<? extends AdapterArtillery> adapterClass = type.getAdapter();
                AdapterArtillery adapt = AdapterBuilder.build(adapterClass, values.get(type));
                //this sets the config here
                if (adapt == null) {
                    plugin.getLogger().warning("Unable to build adapters!");
                    return false;
                }
            }

       return true;

    }

    private static boolean tryLoadDefaults() {
        for (ArtilleryType type: ArtilleryType.values()) {
            Class<? extends AdapterArtillery> adapterClass = type.getAdapter();
            AdapterArtillery adapt = AdapterBuilder.build(adapterClass, null);
            if (adapt == null)
                return false;
        }
        return true;
    }








    private static @Nullable Map<String, Object> getValue(ArtillerySchema property, TomlParseResult res) {
       Map<String, Object> map = new HashMap<>();
        ArtilleryType type = property.getType();
        try {
            SchemaKey[] keys = property.getConfigValues();

            for (SchemaKey key: keys) {
                Object value = get(key.getClazz(), res, type.getId() + "." + key.getLabel());

                if (value == null) {
                    throw new IllegalArgumentException("Invalid value for: "+key.getLabel());
                }


                map.put(key.getLabel(), value);
            }
        }
        catch (Exception e) {
            Plugin plugin = FortressGuns.getInstance();
            plugin.getLogger().warning("Failed to read the config section for the artillery: "+type.getId()+" due to: "+e.getMessage());
            return null;
        }
        return map;
    }


    private static @Nullable Object get(Class<?> clazz, TomlParseResult result, String label) {

        try {
            if (clazz == Integer.class) {
                return result.getLong(label);
            } else if (clazz == Float.class || clazz == Double.class) {
                try {
                    return result.getDouble(label);
                } catch (TomlInvalidTypeException invalid) {

                    try {
                        return result.getLong(label);
                    } catch (Exception ignored) {
                        return null;
                    }
                }
            } else if (clazz == Boolean.class) {
                return result.getBoolean(label);
            }
            else if (clazz == String.class) {
                return result.getString(label);
            }

        }
        catch (Exception e) {
            return null;
        }

        return null;

    }
}
