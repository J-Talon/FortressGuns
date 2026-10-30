package me.camm.productions.fortressguns.Util.Config.v2;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Config.v1.*;
import org.bukkit.plugin.Plugin;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.logging.Logger;

public class FileManager {
    public enum ResourceFile {


        CONFIG("ArtilleryConfig.toml");
        ResourceFile(String file) {
            this.file = file;
        }

        public String get() {
            return file;
        }

        private final String file;
    }

    public static String getFilePath(){
        return getFolderPath() + File.separator;
    }


    public static String getFolderPath(){
        Plugin plugin = FortressGuns.getInstance();
        return plugin.getDataFolder().getParentFile().getAbsolutePath() + File.separator + plugin.getName();
    }


    public static File initResource(ResourceFile res) throws IOException {

        String name = res.get();
        InputStream stream = FortressGuns.getInstance().getResource(name);

        File folder = new File(getFolderPath());
        Path path = Paths.get(getFolderPath());

        Files.createDirectories(path);


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
            File file = initResource(ResourceFile.CONFIG);
            Path path = Paths.get(file.getPath());
            TomlParseResult res = Toml.parse(path);

            if (res.hasErrors()) {
                logger.warning("Artillery config failed to load due to TOML errors in the file, using defaults...");
                return;
            }

            ConfigManager manager = ConfigManager.getInstance();
            manager.load( logger, res);
            logger.info("Config loaded!");
        }
        catch (IOException e) {
            logger.warning("Could not load config due to IO error: "+e.getMessage()+"...Using default config.");

        }

    }

}
