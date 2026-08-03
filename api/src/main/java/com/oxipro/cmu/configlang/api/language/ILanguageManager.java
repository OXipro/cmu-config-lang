package com.oxipro.cmu.configlang.api.language;

import java.util.UUID;

public interface ILanguageManager {
    void addLanguage(String id, ILanguage language);

    ILanguage getLanguage(String id);

    ILanguage getPlayerLanguage(UUID playerUUID);
}
