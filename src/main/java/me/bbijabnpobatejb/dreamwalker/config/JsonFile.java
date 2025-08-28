package me.bbijabnpobatejb.dreamwalker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JsonFile<T> {
    static final Gson GSON = new GsonBuilder().create();
    static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    final String fileName;
    @Getter
    @Setter
    T data;
    final Supplier<T> supplier;
    final Class<T> clazz;
    final String path;

    public JsonFile(String path, String fileName, Class<T> clazz, Supplier<T> supplier) {
        this.fileName = fileName;
        this.clazz = clazz;
        this.supplier = supplier;
        this.path = path + fileName + ".json";
    }

    void init() {
        this.data = supplier.get();
        save();
    }

    public void createFile() {
        File file = new File(path);
        if (file.exists() && !file.isFile()) {
            DreamWalker.getLogger().error("Path exists but is not a file: {}", path);
        }
        try {
            if (!file.exists() && !file.createNewFile()) {
                DreamWalker.getLogger().error("Failed to create file: {}", path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public T load() {
        try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            this.data = GSON.fromJson(reader, clazz);
            if (this.data == null) {
                init();
            }
            return this.data;
        } catch (IOException e) {
            DreamWalker.getLogger().error("Failed to load file {}", path);
            e.printStackTrace();
        }
        return this.data;
    }

    public T save() {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8)) {
            String escapedJson = PRETTY_GSON.toJson(this.data);
            String unescapedJson = StringEscapeUtils.unescapeJava(escapedJson);
            writer.write(unescapedJson);
        } catch (IOException e) {
            DreamWalker.getLogger().error("File {} not be saved", path);
            e.printStackTrace();
        }
        return this.data;
    }

}


