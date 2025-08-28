package me.bbijabnpobatejb.dreamwalker.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.config.object.GlobalAliasConfig;
import me.bbijabnpobatejb.dreamwalker.config.object.PlayerAliasConfig;
import me.bbijabnpobatejb.dreamwalker.config.object.SimpleConfig;

import java.io.File;
import java.util.Arrays;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonHandler implements IDataHandler {

    String folderPath;
    @Getter
    JsonFile<PlayerAliasConfig> playerAliasConfig;
    @Getter
    JsonFile<GlobalAliasConfig> globalAliasConfig;
    @Getter
    JsonFile<SimpleConfig> config;

    List<JsonFile<?>> jsonFiles;

    public JsonHandler(File modConfigurationDirectory, String folderName) {
        this.folderPath = new File(modConfigurationDirectory, folderName).getPath();
        this.playerAliasConfig = new JsonFile<>(this.folderPath + "/", "player_alias_config", PlayerAliasConfig.class, PlayerAliasConfig::new);
        this.globalAliasConfig = new JsonFile<>(this.folderPath + "/", "global_alias_config", GlobalAliasConfig.class, GlobalAliasConfig::new);
        this.config = new JsonFile<>(this.folderPath + "/", "config", SimpleConfig.class, SimpleConfig::new);
        this.jsonFiles = Arrays.asList(playerAliasConfig, globalAliasConfig, config);

        createFolder();
        load();
    }


    @Override
    public File createFolder() {
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        for (JsonFile<?> jsonFile : jsonFiles) {
            jsonFile.createFile();
        }
        return folder;
    }

    @Override
    public void load() {
        for (JsonFile<?> jsonFile : jsonFiles) {
            jsonFile.load();
        }
    }

    @Override
    public void save() {
        for (JsonFile<?> jsonFile : jsonFiles) {
            jsonFile.save();
        }
    }


}
