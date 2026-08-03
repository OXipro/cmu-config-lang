package com.oxipro.cmu.configlang.api.language.detection;

import java.util.Locale;

public interface ILanguageDetector<T> {
    /**
     * get the locale of the player (ip or client)
     * @param source can be an ip, player, uuid, depending on the implementation
     * @return the locale
     */
    Locale detect(T source);
}
