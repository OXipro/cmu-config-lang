package com.oxipro.cmu.configLang.language;


import com.oxipro.cmu.configLang.config.ConfigFile;

public class DynamicLanguage implements ILanguage {
    private final ConfigFile config;
    private final String id;
    private final String fancyName;

    public DynamicLanguage(ConfigFile config, String id) {
        this.config = config;
        this.id = id;
        this.fancyName = config.getString(DefaultLanguagePaths.LANGUAGE_FANCY_NAME);
    }

    @Override
    public String getId() { return id; }

    @Override
    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    @Override
    public String getFancyName() { return fancyName; }

    @Override
    public String getMessage(String path) {
        return config.get().getString(path, path);
    }

    @Override
    public void save(boolean copyDefaults) {
        config.save();
        config.get().options().copyDefaults(copyDefaults);
    }

    @Override
    public ConfigFile getConfig() {
        return config;
    }
}
