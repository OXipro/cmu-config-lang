package com.oxipro.cmu.configlang.minestom.language;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import net.minestom.server.entity.Player;

import java.util.Locale;

public class ClientLocaleDetector implements ILanguageDetector<Player> {

    @Override
    public Locale detect(Player player) {
        if (player == null) return Locale.ENGLISH;
        Locale locale = player.getLocale();
        return locale != null ? locale : Locale.ENGLISH;
    }
}
