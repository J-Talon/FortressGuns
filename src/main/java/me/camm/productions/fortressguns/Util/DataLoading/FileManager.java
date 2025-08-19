package me.camm.productions.fortressguns.Util.DataLoading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import me.camm.productions.fortressguns.Artillery.Entities.Generation.ConstructType;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.DataLoading.Config.*;
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

    static Map<String, ConstructType> types = new HashMap<>();


    public enum Resource {

        //we're not gonna do skins cause I'm planning on
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



    public enum IndependentConfig {
        CONTACT("contactDamage", ConfigArtilleryContact.class),
        EXPLOSION("explosions", ConfigArtilleryExplosions.class),
        PROJECTILE("projectiles", ConfigArtilleryProjectiles.class),
        GENERAL("general", ConfigGeneral.class);

        private IndependentConfig(String id, Class<? extends ConfigObject> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        private final String id;
        private final Class<? extends ConfigObject> clazz;

        public String getId() {
            return id;
        }

        public Class<? extends ConfigObject> getClazz() {
            return clazz;
        }
    }

    static {
        for (ConstructType t : ConstructType.values()) {
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



    public static void loadArtilleryConfig() {

        Plugin plugin = FortressGuns.getInstance();
        Logger logger = plugin.getLogger();


        try {
            File file = initResource(Resource.CONFIG);
            Path path = Paths.get(file.getPath());
            TomlParseResult res = Toml.parse(path);

            if (res.hasErrors()) {
                logger.warning("Artillery config failed to load due to TOML errors in the file, using defaults...");
                return;
            }

            String json = res.toJson();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);



            for (ConstructType type: ConstructType.values()) {
                mapper.registerSubtypes(new NamedType(type.getAdapter(), type.getId()));
                try {
                    ConfigObject co = mapper.treeToValue(node.get(type.getId()),type.getAdapter());
                    boolean result = co.apply();
                    if (!result)
                        throw new IllegalArgumentException("Invalid config value");

                    logger.info("Successfully loaded config for: "+type.getId());
                }
                catch (JsonProcessingException | IllegalArgumentException e) {
                    logger.warning("Failed to load "+ type.getId() +": "+e.getMessage() +" | Will attempt to use defaults.");
                }
            }


            for (IndependentConfig config: IndependentConfig.values()) {
                mapper.registerSubtypes(new NamedType(config.getClazz(), config.getId()));
                try {
                    ConfigObject co = mapper.treeToValue(node.get(config.getId()), config.getClazz());

                    if (co == null) {
                        throw new IllegalArgumentException("config section for "+config.getId()+" doesn't contain a loadable class");
                    }

                    if (!co.apply()) {
                        throw new IllegalArgumentException("Invalid config value");
                    }
                    logger.info("Successfully loaded independent section: "+config.getId());
                }
                catch (JsonProcessingException | IllegalArgumentException e) {
                    logger.warning("Failed to load "+ config.getId() +" due to: "+e.getMessage() +" | Using defaults.");
                }
            }


        }
        catch (IOException e) {
            logger.warning("Could not load config due to IO error:"+e.getMessage()+"...Using default config.");
        }

    }

}
