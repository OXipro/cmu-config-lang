package com.oxipro.cmu.configlang.api.language;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public interface ILanguageManager {

    void addLanguage(ILanguage language);

    void addLanguage(Locale locale, ILanguage language);

    ILanguage getLanguage(Locale locale);

    ILanguage getPlayerLanguage(UUID playerUUID);

    Set<Locale> getLocales();

    Locale getFallbackLocale();

    void setFallbackLocale(Locale fallbackLocale);
}
