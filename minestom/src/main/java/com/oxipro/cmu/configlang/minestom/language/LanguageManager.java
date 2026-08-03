package com.oxipro.cmu.configlang.minestom.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.AddressLanguageProvider;
import com.oxipro.cmu.configlang.common.langProvider.IpLanguageDetector;
import com.oxipro.cmu.configlang.common.langProvider.PlayerLanguageResolver;
import net.minestom.server.entity.Player;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class LanguageManager extends com.oxipro.cmu.configlang.standalone.language.LanguageManager {

    private final PlayerLanguageResolver<Player> languageResolver;

    public LanguageManager(File languagesFolder, Map<String, ILanguage> defaultLanguages, ILanguageDB languageDB, IpLanguageDetector ipLanguageDetector) {
        super(languagesFolder, defaultLanguages, languageDB);
        this.languageResolver = new PlayerLanguageResolver<>(languageDB, Player::getUuid, buildProviders(ipLanguageDetector));
    }

    private static List<ILanguageDetector<Player>> buildProviders(IpLanguageDetector ipLanguageDetector) {
        if (ipLanguageDetector == null) return List.of(new ClientLocaleDetector());
        return List.of(new ClientLocaleDetector(), new AddressLanguageProvider<>(ipLanguageDetector, LanguageManager::getPlayerAddress));
    }

    public ILanguage getPlayerLanguage(Player player) {
        if (player == null) return null;
        return getLanguage(languageResolver.resolveLangId(player, getLanguageIds()));
    }

    private static String getPlayerAddress(Player player) {
        SocketAddress address = player.getPlayerConnection().getRemoteAddress();
        if (!(address instanceof InetSocketAddress inetAddress) || inetAddress.getAddress() == null) return null;
        return inetAddress.getAddress().getHostAddress();
    }
}
