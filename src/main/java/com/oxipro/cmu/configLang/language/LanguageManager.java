package com.oxipro.cmu.configLang.language;

import com.oxipro.cmu.configLang.config.ConfigFile;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LanguageManager {

    private final Map<String, ILanguage> languages = new HashMap<>();
    private final JavaPlugin plugin;
    private final PlayerSettingCache playerSettingCache;
    private final PlayerSettingRepository playerSettingRepository;
    private Map<String, Language> defaultLanguages;
    private final IPLanguage iPLanguage;

    public LanguageManager(JavaPlugin plugin, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository, Map<String, Language> defaultLanguages, IPLanguage iPLanguage) {
        this.plugin = plugin;
        this.playerSettingCache = playerSettingCache;
        this.playerSettingRepository = playerSettingRepository;
        this.defaultLanguages = defaultLanguages;
        this.iPLanguage = iPLanguage;
        loadLanguages();
    }

    private void loadLanguages() {
        if (defaultLanguages != null) {
            for (Language defaultLang : defaultLanguages.values()) {
                addLanguage(defaultLang.getId(), defaultLang);
            }
        }

        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String name = file.getName().replace(".yml", "").toLowerCase();
            if (defaultLanguages.containsKey(name)) continue;
            ConfigFile cf = new ConfigFile(plugin, "languages/" + file.getName());
            addLanguage(name, new Language(cf) {
            });
        }
    }

    public void addLanguage(String id, ILanguage language) {
        languages.put(id.toLowerCase(), language);
    }

    public ILanguage getLanguage(String id) {
        if (id == null) return languages.get("EN-en");
        return languages.getOrDefault(id.toLowerCase(), languages.get("EN-en"));
    }

    public ILanguage getPlayerLanguage(UUID playerUUID) {
        if (playerUUID == null) return null;
        String langIso = playerSettingCache.get(playerUUID, "lang_iso");
        return getLanguage(langIso);
    }

    /** get The playerLanguage from the db if he exists
     * Can get IPLanguage if player isn't loaded in db
     * @param player
     * @return
     */
    public ILanguage getPlayerLanguage(Player player) {
        if (player == null) return null;
        String langIso;
        if (playerSettingRepository.exists(player.getUniqueId(), "lang_iso")) {
            langIso = playerSettingCache.get(player.getUniqueId(), "lang_iso");
        } else {
            langIso = getIPLangIso(player);
        }
        return getLanguage(langIso);
    }

    public String getIPLangIso(Player player) {
        if (iPLanguage == null) return Locale.ENGLISH.getLanguage();
        Locale locale = iPLanguage.getLocaleFromPlayer(player);
        return locale.getISO3Language();
    }

}
