package com.oxipro.cmu.configlang.bukkit;

import com.oxipro.cmu.configlang.api.ILanguage;
import com.oxipro.cmu.configlang.bukkit.language.LanguageManager;
import com.oxipro.cmu.configlang.common.langProvider.IPLanguage;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class ConfigLang {

    private final JavaPlugin plugin;
    private final PlayerSettingCache playerSettingCache;
    private final PlayerSettingRepository playerSettingRepository;

    private LanguageManager languageManager;
    private IPLanguage iPLanguage;

    public ConfigLang(JavaPlugin plugin, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this.plugin = plugin;
        this.playerSettingCache = playerSettingCache;
        this.playerSettingRepository = playerSettingRepository;
    }

    public void init(Map<String, ILanguage> defaultLangCountryMap, boolean ipLanguage) {
        if (ipLanguage) {
            this.iPLanguage = new IPLanguage();
        }
        languageManager = new LanguageManager(plugin, playerSettingCache, playerSettingRepository, defaultLangCountryMap, iPLanguage);
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
