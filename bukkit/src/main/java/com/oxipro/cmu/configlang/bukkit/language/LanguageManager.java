package com.oxipro.cmu.configlang.bukkit.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.ILanguageManager;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.api.language.Locales;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.bukkit.config.ConfigFile;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class LanguageManager implements ILanguageManager {

    private final Map<Locale, ILanguage> languages = new HashMap<>();
    private final JavaPlugin plugin;
    private final ILanguageDB languageDB;
    private final Map<Locale, ILanguage> defaultLanguages;
    private final PlayerLanguageResolver<Player> languageResolver;
    private Locale fallbackLocale;

    public LanguageManager(JavaPlugin plugin, ILanguageDB languageDB, Map<Locale, ILanguage> defaultLanguages, LanguageSettings settings) {
        this.plugin = plugin;
        this.languageDB = languageDB;
        this.defaultLanguages = defaultLanguages;
        LanguageSettings effective = settings != null ? settings : LanguageSettings.defaults();
        this.fallbackLocale = Objects.requireNonNull(effective.getFallbackLocale(), "fallbackLocale");
        this.languageResolver = new PlayerLanguageResolver<>(languageDB, Player::getUniqueId, buildProviders(effective));
        loadLanguages();
    }

    private List<ILanguageDetector<Player>> buildProviders(LanguageSettings settings) {
        List<ILanguageDetector<Player>> providers = new ArrayList<>();
        if (settings.isClientLocaleEnabled()) {
            providers.add(new ClientLocaleDetector());
        }
        if (settings.isIpLanguageEnabled()) {
            providers.add(new AddressLanguageProvider<>(new IpLanguageDetector(), LanguageManager::getPlayerAddress));
        }
        return providers;
    }

    private void loadLanguages() {
        if (defaultLanguages != null) {
            for (Map.Entry<Locale, ILanguage> entry : defaultLanguages.entrySet()) {
                addLanguage(entry.getKey(), entry.getValue());
            }
        }

        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles((dir, name) -> isLanguageFile(name));
        if (files == null) return;

        for (File file : files) {
            Locale locale = localeFromFileName(file.getName());
            if (locale == null || languages.containsKey(locale)) continue;
            ConfigFile cf = new ConfigFile(plugin, "languages/" + file.getName());
            addLanguage(locale, new Language(locale, cf));
        }
    }

    @Override
    public void addLanguage(ILanguage language) {
        if (language == null) return;
        addLanguage(language.getLocale(), language);
    }

    @Override
    public void addLanguage(Locale locale, ILanguage language) {
        languages.put(locale, language);
    }

    @Override
    public ILanguage getLanguage(Locale locale) {
        ILanguage fallback = languages.get(fallbackLocale);
        if (locale == null) return fallback;
        return languages.getOrDefault(locale, fallback);
    }

    @Override
    public Locale getFallbackLocale() {
        return fallbackLocale;
    }

    @Override
    public void setFallbackLocale(Locale fallbackLocale) {
        this.fallbackLocale = Objects.requireNonNull(fallbackLocale, "fallbackLocale");
    }

    @Override
    public Set<Locale> getLocales() {
        return languages.keySet();
    }

    @Override
    public ILanguage getPlayerLanguage(UUID playerUUID) {
        if (playerUUID == null) return null;
        if (languageDB.has(playerUUID)) {
            return getLanguage(languageDB.getLocale(playerUUID));
        }
        return getLanguage(null);
    }

    public ILanguage getPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.resolveLocale(player, languages.keySet()));
    }

    /** Client / IP / fallback only — ignores DB (e.g. offline crack guests). */
    public ILanguage detectPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.detectLocale(player, languages.keySet()));
    }

    private static String getPlayerAddress(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address == null || address.getAddress() == null) return null;
        return address.getAddress().getHostAddress();
    }

    private static boolean isLanguageFile(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".yml") || lower.endsWith(".yaml");
    }

    private static Locale localeFromFileName(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        String base;
        if (lower.endsWith(".yaml")) {
            base = fileName.substring(0, fileName.length() - 5);
        } else if (lower.endsWith(".yml")) {
            base = fileName.substring(0, fileName.length() - 4);
        } else {
            return null;
        }
        try {
            return Locales.parse(base);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
