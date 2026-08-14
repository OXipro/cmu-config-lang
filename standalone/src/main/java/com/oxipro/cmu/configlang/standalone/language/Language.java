package com.oxipro.cmu.configlang.standalone.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.Locales;
import com.oxipro.cmu.configlang.api.language.defaults.DefaultLanguagePaths;
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Language implements ILanguage {

    private final ConfigFile config;
    private final Locale locale;

    public Language(ConfigFile config) {
        this(Locales.parse(config.getFileName()), config);
    }

    public Language(Locale locale, ConfigFile config) {
        this.config = config;
        this.locale = Objects.requireNonNull(locale, "locale");
        saveDefaults();
    }

    protected void saveDefaults() {}

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    @Override
    public String getFancyName() {
        return config.getString(DefaultLanguagePaths.LANGUAGE_FANCY_NAME);
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
        config.save(copyDefaults);
    }

    @Override
    public ConfigFile getConfig() {
        return config;
    }
}
