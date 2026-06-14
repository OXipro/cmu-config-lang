package com.oxipro.cmu.configLang.language;

import com.oxipro.cmu.configLang.config.ConfigFile;

import java.util.List;

public class Language implements ILanguage {

    protected final ConfigFile config;
    private final String fancyName;

    public Language(ConfigFile config) {
        this.config = config;
        saveDefaults();
        config.save();
        this.fancyName = config.getString(DefaultLanguagePaths.LANGUAGE_FANCY_NAME);
    }

    protected void saveDefaults() {}

    public String getId() { return config.get().getName(); }

    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    public String getFancyName() { return fancyName; }

    public List<String> getMessageAsList(String path) {
        if (config.get().contains(path)) return config.get().getStringList(path);
        return List.of(path);
    }

    public String getMessage(String path) {
        if (config.get().contains(path)) return config.getString(path);
        return path;
    }

    public void save(boolean copyDefaults) {
        config.save();
        config.get().options().copyDefaults(copyDefaults);
    }

    public ConfigFile getConfig() {
        return config;
    }
}
