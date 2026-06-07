package com.oxipro.cmu.configLang;

import com.oxipro.cmu.configLang.language.IPLanguage;
import com.oxipro.cmu.configLang.language.Language;
import com.oxipro.cmu.configLang.language.LanguageManager;
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

    public void init(Map<String, Language> defaultLang, boolean apiLanguage) {
        if (apiLanguage) {
            this.iPLanguage = new IPLanguage();
        }
        languageManager = new LanguageManager(plugin, playerSettingCache, playerSettingRepository, defaultLang, iPLanguage);
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
