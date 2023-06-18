package me.camm.productions.fortressguns.Util;

import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Attribute.AttributeManager;
import org.bukkit.plugin.Plugin;
import org.tomlj.Toml;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    static Map<String, ArtilleryType> types = new HashMap<>();
    static final String FILE = "ArtilleryConfig.toml";

    static {
        for (ArtilleryType t : ArtilleryType.values()) {
            types.put(t.getId(),t);
        }
    }

    public static String getFilePath(){
        Plugin plugin = FortressGuns.getInstance();
       return plugin.getDataFolder().getParentFile().getAbsolutePath()+ "\\PluginMetrics";
    }


    public static File copyResource() throws IOException {
        InputStream stream = FortressGuns.getInstance().getResource(FILE);

        if (stream == null) {
            throw new NoSuchFileException("Artillery config not found");
        }

        File file = new File(getFilePath()+FILE);
        if (file.exists())
            return file;

        Files.copy(stream,file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    public static AttributeManager createManager() throws Exception {
        AttributeManager manager = AttributeManager.getInstance();
        File file = new File(getFilePath()+FILE);
        if (!file.exists())
            throw new IllegalStateException("Could not find artillery config");

        TomlParseResult res = Toml.parse(file.toPath());
        if (res.hasErrors()) {
            for (TomlParseError e : res.errors()) {
                e.printStackTrace();
            }
        }

        for (String key: res.keySet()) {
            Map<String, Object> map = res.getTable(key).toMap();
            if (types.containsKey(key)) {
                manager.addAttribute(map, types.get(key).getClazz());
            }
        }

        return manager;

    }
}
