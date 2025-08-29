package me.bbijabnpobatejb.dreamwalker.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.Alias;
import me.bbijabnpobatejb.dreamwalker.config.model.GlobalAliasConfig;
import me.bbijabnpobatejb.dreamwalker.config.model.PlayerAliasConfig;
import me.bbijabnpobatejb.dreamwalker.config.model.SimpleConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonHandler implements IDataHandler {

    String folderPath;
    File playerFolder;
    @Getter
    Map<String, JsonFile<PlayerAliasConfig>> playersAliasConfig = new HashMap<>();
    @Getter
    JsonFile<GlobalAliasConfig> globalAliasConfig;
    @Getter
    JsonFile<SimpleConfig> config;

    List<JsonFile<?>> jsonFiles;

    public JsonHandler(File modConfigurationDirectory, String folderName) {
        this.folderPath = new File(modConfigurationDirectory, folderName).getPath();
        this.playerFolder = new File(folderPath + "/players/");
        this.globalAliasConfig = new JsonFile<>(this.folderPath + "/", "global_alias_config", GlobalAliasConfig.class, GlobalAliasConfig::new);
        this.config = new JsonFile<>(this.folderPath + "/", "config", SimpleConfig.class, SimpleConfig::new);
        this.jsonFiles = Arrays.asList(globalAliasConfig, config);

        createFolder();
        load();
    }

   public void loadPlayers() {


        // Создаём папку, если она не существует
        if (!playerFolder.exists()) {
            boolean created = playerFolder.mkdirs();
            if (!created) {
                DreamWalker.getLogger().error("Failed to create player config folder: {}", playerFolder.getAbsolutePath());
                return;
            }
        }

        File[] files = playerFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files == null) return;

        if (files.length == 0) {
            val playerName = "Player123";
            JsonFile<PlayerAliasConfig> json = createPlayerAliasJsonFile(playerName, new PlayerAliasConfig(
                    Arrays.asList(
                            new Alias("сила", "Сила", "+3",
                                    Arrays.asList("/roll 1d20+3", "/roll {args}")
                            ),
                            new Alias("ловкость", "Ловкость", "+2",
                                    Arrays.asList("/roll 1d20+3", "/roll {args}")
                            )
                    )));

            playersAliasConfig.put(playerName, json);
            savePlayers();
        }

        for (File file : files) {
            String name = file.getName();

            if (name.startsWith("_") || name.equalsIgnoreCase("template.json")) continue;

            String playerName = name.replace(".json", "");

            JsonFile<PlayerAliasConfig> playerConfig = createPlayerAliasJsonFile(playerName, null);
            playersAliasConfig.put(playerName, playerConfig);
        }
    }

    public JsonFile<PlayerAliasConfig> createPlayerAliasJsonFile(String playerName, @Nullable PlayerAliasConfig data) {
        JsonFile<PlayerAliasConfig> playerConfig = new JsonFile<>(
                playerFolder.getPath() + "/", playerName, PlayerAliasConfig.class, PlayerAliasConfig::new
        );

        playerConfig.createFile();
        playerConfig.load();
        if (data != null) {
            playerConfig.setData(data);
        }
        return playerConfig;
    }

    void savePlayers() {
        for (Map.Entry<String, JsonFile<PlayerAliasConfig>> entry : playersAliasConfig.entrySet()) {
            JsonFile<PlayerAliasConfig> playerConfig = entry.getValue();
            playerConfig.save();
        }
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
        loadPlayers();
    }

    @Override
    public void save() {
        for (JsonFile<?> jsonFile : jsonFiles) {
            jsonFile.save();
        }
        savePlayers();
    }


}
