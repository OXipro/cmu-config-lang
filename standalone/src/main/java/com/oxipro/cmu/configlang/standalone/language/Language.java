package com.oxipro.cmu.configlang.standalone.language;

import com.oxipro.cmu.configlang.api.language.defaults.DefaultLanguagePaths;
import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

import java.util.List;

public class Language implements ILanguage {

    private final ConfigFile config;
    private final String fancyName;

    public Language(ConfigFile config) {
        this.config = config;
        config.save();
        this.fancyName = config.getString(DefaultLanguagePaths.LANGUAGE_FANCY_NAME);
    }

    @Override
    public String getId() {
        return config.getFileName();
    }

    @Override
    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    @Override
    public String getFancyName() {
        return fancyName;
    }

    @Override
    public List<String> getMessageAsList(String path) {
        if (config.contains(path)) return config.getStringList(path);
        return List.of(path);
    }

    @Override
    public String getMessage(String path) {
        if (config.contains(path)) return config.getString(path);
        return path;
    }

    @Override
    public void save(boolean copyDefaults) {
        config.save();
    }

    @Override
    public ConfigFile getConfig() {
        return config;
    }
}
