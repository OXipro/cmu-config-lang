package com.oxipro.cmu.configlang.bukkit;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.bukkit.language.LanguageManager;
import com.oxipro.cmu.configlang.common.languagedb.CSSDBLanguageDB;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Map;

public class ConfigLang {

    private final JavaPlugin plugin;
    private final CSSDBLanguageDB playerLanguageStore;
    private final LanguageSettings settings;

    private LanguageManager languageManager;

    public ConfigLang(JavaPlugin plugin, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this(plugin, playerSettingCache, playerSettingRepository, LanguageSettings.defaults());
    }

    public ConfigLang(JavaPlugin plugin, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository, LanguageSettings settings) {
        this.plugin = plugin;
        this.playerLanguageStore = new CSSDBLanguageDB(playerSettingCache, playerSettingRepository);
        this.settings = settings != null ? settings : LanguageSettings.defaults();
    }

    public void init(Map<Locale, ILanguage> defaultLanguages) {
        init(defaultLanguages, settings);
    }

    public void init(Map<Locale, ILanguage> defaultLanguages, LanguageSettings settings) {
        LanguageSettings effective = settings != null ? settings : this.settings;
        languageManager = new LanguageManager(plugin, playerLanguageStore, defaultLanguages, effective);
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
