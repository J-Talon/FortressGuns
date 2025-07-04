package me.camm.productions.fortressguns.Util.DataLoading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import me.camm.productions.fortressguns.Artillery.Entities.Components.ArtilleryType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigGeneral;
import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigObject;
import org.bukkit.plugin.Plugin;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileManager {

    static Map<String, ArtilleryType> types = new HashMap<>();


    enum Resource {

        ///we're not gonna do skins cause I'm planning on
        //doing optional resource pack models
        CONFIG("ArtilleryConfig.toml"),
        SAVES("SavedArtillery.toml");
        Resource(String file) {
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

        Plugin plugin = FortressGuns.getInstance();
        Logger logger = plugin.getLogger();


        try {
            File file = initResource(Resource.CONFIG);
            Path path = Paths.get(file.getPath());
            TomlParseResult res = Toml.parse(path);

            if (res.hasErrors()) {
                logger.warning("Artillery config failed to load due to TOML errors in the file, using defaults...");
                return true;
            }

            String json = res.toJson();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);



            for (ArtilleryType type: ArtilleryType.values()) {
                mapper.registerSubtypes(new NamedType(type.getAdapter(), type.getId()));
                try {
                    ConfigObject co = mapper.treeToValue(node.get(type.getId()),type.getAdapter());
                    boolean result = co.apply();
                    if (!result)
                        throw new IllegalArgumentException("Invalid config value");

                    logger.info("Loaded config for: "+type.getId());
                }
                catch (JsonProcessingException | IllegalArgumentException e) {
                    logger.warning("Failed to load "+ type.getId() +" due to: "+e.getMessage() +" Using defaults.");
                }
            }



            try {
                String genId = "general";
                Class<? extends ConfigObject> genClass = ConfigGeneral.class;
                mapper.registerSubtypes(new NamedType(genClass, genId));
                ConfigObject genCo = mapper.treeToValue(node.get(genId),genClass);

                if (!genCo.apply()) {
                    throw new IllegalArgumentException("Invalid general config value");
                }

                logger.info("Loaded general options");
              }
            catch (JsonProcessingException e) {
                logger.warning("Failed to load general options. Using defaults.");
            }


        }
        catch (IOException e) {
            logger.warning("Could not load config due to IO error:"+e.getMessage()+"...Using default config.");
        }

       return true;
    }

}
