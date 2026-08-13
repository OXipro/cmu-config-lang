package com.oxipro.cmu.configlang.api.language.db;

import java.util.Locale;
import java.util.UUID;

public interface ILanguageDB {
    boolean has(UUID playerId);

    Locale getLocale(UUID playerId);

    void setLocale(UUID playerId, Locale locale);
}
