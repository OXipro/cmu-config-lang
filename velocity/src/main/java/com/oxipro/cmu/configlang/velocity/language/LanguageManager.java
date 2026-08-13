package com.oxipro.cmu.configlang.velocity.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import com.velocitypowered.api.proxy.Player;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageManager extends com.oxipro.cmu.configlang.standalone.language.LanguageManager {

    private final PlayerLanguageResolver<Player> languageResolver;

    public LanguageManager(File languagesFolder, Map<Locale, ILanguage> defaultLanguages, ILanguageDB languageDB, LanguageSettings settings) {
        super(languagesFolder, defaultLanguages, languageDB, settings);
        LanguageSettings effective = settings != null ? settings : LanguageSettings.defaults();
        this.languageResolver = new PlayerLanguageResolver<>(languageDB, Player::getUniqueId, buildProviders(effective));
    }

    private static List<ILanguageDetector<Player>> buildProviders(LanguageSettings settings) {
        List<ILanguageDetector<Player>> providers = new ArrayList<>();
        if (settings.isClientLocaleEnabled()) {
            providers.add(new ClientLocaleDetector());
        }
        if (settings.isIpLanguageEnabled()) {
            providers.add(new AddressLanguageProvider<>(new IpLanguageDetector(), LanguageManager::getPlayerAddress));
        }
        return providers;
    }

    public ILanguage getPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.resolveLocale(player, getLocales()));
    }

    // does not use db
    public ILanguage detectPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.detectLocale(player, getLocales()));
    }

    private static String getPlayerAddress(Player player) {
        InetSocketAddress address = player.getRemoteAddress();
        if (address == null || address.getAddress() == null) return null;
        return address.getAddress().getHostAddress();
    }
}
