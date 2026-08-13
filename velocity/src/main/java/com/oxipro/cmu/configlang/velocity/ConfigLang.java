package com.oxipro.cmu.configlang.velocity;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.common.languagedb.CSSDBLanguageDB;
import com.oxipro.cmu.configlang.velocity.language.LanguageManager;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class ConfigLang {

    private final File dataFolder;
    private final CSSDBLanguageDB playerLanguageStore;
    private final LanguageSettings settings;

    private LanguageManager languageManager;

    public ConfigLang(File dataFolder, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this(dataFolder, playerSettingCache, playerSettingRepository, LanguageSettings.defaults());
    }

    public ConfigLang(File dataFolder, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository, LanguageSettings settings) {
        this.dataFolder = dataFolder;
        this.playerLanguageStore = new CSSDBLanguageDB(playerSettingCache, playerSettingRepository);
        this.settings = settings != null ? settings : LanguageSettings.defaults();
    }

    public void init(Map<Locale, ILanguage> defaultLanguages) {
        init(defaultLanguages, settings);
    }

    public void init(Map<Locale, ILanguage> defaultLanguages, LanguageSettings settings) {
        LanguageSettings effective = settings != null ? settings : this.settings;
        File languagesFolder = new File(dataFolder, "languages");
        languageManager = new LanguageManager(languagesFolder, defaultLanguages, playerLanguageStore, effective);
    }

    @Deprecated
    public void init(Map<Locale, ILanguage> defaultLanguages, boolean ipLanguage) {
        init(defaultLanguages, LanguageSettings.builder()
                .clientLocale(true)
                .ipLanguage(ipLanguage)
                .fallbackLocale(settings.getFallbackLocale())
                .build());
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public LanguageSettings getSettings() {
        return settings;
    }
}
