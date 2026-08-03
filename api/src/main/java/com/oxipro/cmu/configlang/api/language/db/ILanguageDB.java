package com.oxipro.cmu.configlang.api.language.db;

import java.util.UUID;

public interface ILanguageDB {
    boolean has(UUID playerId);

    String getLangId(UUID playerId);

    void setLangId(UUID playerId, String langId);
}
