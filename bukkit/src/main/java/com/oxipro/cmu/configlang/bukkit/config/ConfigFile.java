package com.oxipro.cmu.configlang.bukkit.config;

import com.oxipro.cmu.configlang.api.config.IConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigFile implements IConfigFile {

    private final JavaPlugin plugin;
    private final String configPath;

    private File file;
    private FileConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String filePath) {
        this.plugin = plugin;
        this.configPath = filePath;

        createFile();
        load();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), configPath);

        if (!file.exists()) {
            plugin.saveResource(configPath, false);
        }
    }

    public void load() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefault(String path, Object value) {
        configuration.addDefault(path, value);
    }

    public boolean contains(String path) {
        return configuration.contains(path);
    }

    public FileConfiguration get() {
        return configuration;
    }

    public String getString(String path) {
        return configuration.getString(path);
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    public long getLong(String path) { return configuration.getLong(path); }

    public double getDouble(String path) {
        return configuration.getDouble(path);
    }

    public float getFloat(String path) {
        return (float) configuration.getDouble(path);
    }

    public List<String> getStringList(String path) { return configuration.getStringList(path); }

    public String getFileName() {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

}
