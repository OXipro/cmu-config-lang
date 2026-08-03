package com.oxipro.cmu.configlang.minestom;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.languagedb.CSSDBLanguageDB;
import com.oxipro.cmu.configlang.minestom.language.LanguageManager;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

import java.io.File;
import java.util.Map;

public class ConfigLang {

    private final File dataFolder;
    private final CSSDBLanguageDB playerLanguageStore;

    private LanguageManager languageManager;

    public ConfigLang(File dataFolder, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this.dataFolder = dataFolder;
        this.playerLanguageStore = new CSSDBLanguageDB(playerSettingCache, playerSettingRepository);
    }

    public void init(Map<String, ILanguage> defaultLanguages, boolean ipLanguage) {
        File languagesFolder = new File(dataFolder, "languages");
        IpLanguageDetector ipLanguageDetector = ipLanguage ? new IpLanguageDetector() : null;
        languageManager = new LanguageManager(languagesFolder, defaultLanguages, playerLanguageStore, ipLanguageDetector);
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}
