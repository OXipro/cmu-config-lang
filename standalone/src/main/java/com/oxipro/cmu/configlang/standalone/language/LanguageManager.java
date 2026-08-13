package com.oxipro.cmu.configlang.standalone.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.ILanguageManager;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.api.language.Locales;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.defaults.DefaultValues;
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class LanguageManager implements ILanguageManager {

    private final Map<Locale, ILanguage> languages = new HashMap<>();
    private final File languagesFolder;
    private final ILanguageDB languageDB;
    private Locale fallbackLocale;

    public LanguageManager(File languagesFolder, Map<Locale, ILanguage> defaultLanguages, ILanguageDB languageDB) {
        this(languagesFolder, defaultLanguages, languageDB, DefaultValues.FALLBACK_LOCALE);
    }

    public LanguageManager(File languagesFolder, Map<Locale, ILanguage> defaultLanguages, ILanguageDB languageDB, LanguageSettings settings) {
        this(languagesFolder, defaultLanguages, languageDB, settings != null ? settings.getFallbackLocale() : DefaultValues.FALLBACK_LOCALE);
    }

    public LanguageManager(File languagesFolder, Map<Locale, ILanguage> defaultLanguages, ILanguageDB languageDB, Locale fallbackLocale) {
        this.languagesFolder = languagesFolder;
        this.languageDB = languageDB;
        this.fallbackLocale = Objects.requireNonNull(fallbackLocale, "fallbackLocale");
        loadLanguages(defaultLanguages);
    }

    private void loadLanguages(Map<Locale, ILanguage> defaultLanguages) {
        if (defaultLanguages != null) {
            for (Map.Entry<Locale, ILanguage> entry : defaultLanguages.entrySet()) {
                addLanguage(entry.getKey(), entry.getValue());
            }
        }

        if (!languagesFolder.exists()) languagesFolder.mkdirs();

        File[] files = languagesFolder.listFiles((dir, name) -> isLanguageFile(name));
        if (files == null) return;

        for (File file : files) {
            Locale locale = localeFromFileName(file.getName());
            if (locale == null || languages.containsKey(locale)) continue;
            addLanguage(locale, new Language(locale, new ConfigFile(file)));
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

    public boolean hasLanguage(Locale locale) {
        return locale != null && languages.containsKey(locale);
    }

    @Override
    public Set<Locale> getLocales() {
        return languages.keySet();
    }

    @Override
    public ILanguage getPlayerLanguage(UUID playerId) {
        if (playerId == null) return null;
        if (languageDB != null && languageDB.has(playerId)) {
            return getLanguage(languageDB.getLocale(playerId));
        }
        return getLanguage(null);
    }

    public void setPlayerLanguage(UUID playerId, Locale locale) {
        if (languageDB != null) languageDB.setLocale(playerId, locale);
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
