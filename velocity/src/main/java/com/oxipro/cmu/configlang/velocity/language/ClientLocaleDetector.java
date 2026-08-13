package com.oxipro.cmu.configlang.velocity.language;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import com.velocitypowered.api.proxy.Player;

import java.util.Locale;

public class ClientLocaleDetector implements ILanguageDetector<Player> {

    @Override
    public Locale detect(Player player) {
        if (player == null) return null;
        return player.getPlayerSettings().getLocale();
    }
}
