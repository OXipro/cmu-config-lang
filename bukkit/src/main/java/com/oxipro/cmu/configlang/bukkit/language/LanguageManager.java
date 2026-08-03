package com.oxipro.cmu.configlang.bukkit.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.ILanguageManager;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.defaults.DefaultValues;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.bukkit.config.ConfigFile;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LanguageManager implements ILanguageManager {

    private final Map<String, ILanguage> languages = new HashMap<>();
    private final JavaPlugin plugin;
    private final ILanguageDB languageDB;
    private final Map<String, ILanguage> defaultLanguages;
    private final PlayerLanguageResolver<Player> languageResolver;

    public LanguageManager(JavaPlugin plugin, ILanguageDB languageDB, Map<String, ILanguage> defaultLanguages, IpLanguageDetector ipLanguageDetector) {
        this.plugin = plugin;
        this.languageDB = languageDB;
        this.defaultLanguages = defaultLanguages;
        this.languageResolver = new PlayerLanguageResolver<>(languageDB, Player::getUniqueId, buildProviders(ipLanguageDetector));
        loadLanguages();
    }

    private List<ILanguageDetector<Player>> buildProviders(IpLanguageDetector ipLanguageDetector) {
        if (ipLanguageDetector == null) return List.of(new ClientLocaleDetector());
        return List.of(new ClientLocaleDetector(), new AddressLanguageProvider<>(ipLanguageDetector, LanguageManager::getPlayerAddress));
    }

    private void loadLanguages() {
        if (defaultLanguages != null) {
            for (Map.Entry<String, ILanguage> entry : defaultLanguages.entrySet()) {
                addLanguage(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
            }
        }

        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().replace(".yml", "").toLowerCase(Locale.ROOT);
            if (languages.containsKey(id)) continue;
            ConfigFile cf = new ConfigFile(plugin, "languages/" + file.getName());
            addLanguage(id, new Language(cf));
        }
    }

    @Override
    public void addLanguage(String id, ILanguage language) {
        languages.put(id.toLowerCase(Locale.ROOT), language);
    }

    @Override
    public ILanguage getLanguage(String id) {
        ILanguage fallback = languages.get(DefaultValues.FALLBACK_LANGUAGE_ID);
        if (id == null) return fallback;
        return languages.getOrDefault(id.toLowerCase(Locale.ROOT), fallback);
    }

    @Override
    public ILanguage getPlayerLanguage(UUID playerUUID) {
        if (playerUUID == null) return null;
        if (languageDB.has(playerUUID)) {
            return getLanguage(languageDB.getLangId(playerUUID));
        }
        return getLanguage(null);
    }

    public ILanguage getPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.resolveLangId(player, languages.keySet()));
    }

    private static String getPlayerAddress(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address == null || address.getAddress() == null) return null;
        return address.getAddress().getHostAddress();
    }

}
