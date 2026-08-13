package com.oxipro.cmu.configlang.standalone;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import com.oxipro.cmu.configlang.common.languagedb.CSSDBLanguageDB;
import com.oxipro.cmu.configlang.standalone.language.LanguageManager;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigLang {

    private final File dataFolder;
    private final CSSDBLanguageDB playerLanguageStore;
    private final LanguageSettings settings;

    private PlayerLanguageResolver<PlayerContext> languageResolver;
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
        languageResolver = new PlayerLanguageResolver<>(playerLanguageStore, PlayerContext::id, buildProviders(effective));
    }

    private List<ILanguageDetector<PlayerContext>> buildProviders(LanguageSettings settings) {
        List<ILanguageDetector<PlayerContext>> providers = new ArrayList<>();
        if (settings.isIpLanguageEnabled()) {
            providers.add(new AddressLanguageProvider<>(new IpLanguageDetector(), PlayerContext::ip));
        }
        return providers;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public LanguageSettings getSettings() {
        return settings;
    }

    public ILanguage getPlayerLanguage(PlayerContext player) {
        if (languageManager == null || languageResolver == null) return null;
        return languageManager.getLanguage(languageResolver.resolveLocale(player, languageManager.getLocales()));
    }

    /** Client / IP / fallback only — ignores DB (e.g. offline crack guests). */
    public ILanguage detectPlayerLanguage(PlayerContext player) {
        if (languageManager == null || languageResolver == null) return null;
        return languageManager.getLanguage(languageResolver.detectLocale(player, languageManager.getLocales()));
    }
}
