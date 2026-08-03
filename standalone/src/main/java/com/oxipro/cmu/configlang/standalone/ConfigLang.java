package com.oxipro.cmu.configlang.standalone;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import com.oxipro.cmu.configlang.common.languagedb.CSSDBLanguageDB;
import com.oxipro.cmu.configlang.standalone.language.LanguageManager;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigLang {

    private final File dataFolder;
    private final CSSDBLanguageDB playerLanguageStore;
    private final PlayerLanguageResolver<PlayerContext> languageResolver;

    private LanguageManager languageManager;

    public ConfigLang(File dataFolder, PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this.dataFolder = dataFolder;
        this.playerLanguageStore = new CSSDBLanguageDB(playerSettingCache, playerSettingRepository);

        IpLanguageDetector ipLanguageDetector = new IpLanguageDetector();
        List<ILanguageDetector<PlayerContext>> providers = List.of(
                new AddressLanguageProvider<>(ipLanguageDetector, PlayerContext::ip)
        );
        this.languageResolver = new PlayerLanguageResolver<>(playerLanguageStore, PlayerContext::id, providers);
    }

    public void init(Map<String, ILanguage> defaultLanguages) {
        File languagesFolder = new File(dataFolder, "languages");
        languageManager = new LanguageManager(languagesFolder, defaultLanguages, playerLanguageStore);
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ILanguage getPlayerLanguage(PlayerContext player) {
        return languageManager.getLanguage(languageResolver.resolveLangId(player, languageManager.getLanguageIds()));
    }
}
