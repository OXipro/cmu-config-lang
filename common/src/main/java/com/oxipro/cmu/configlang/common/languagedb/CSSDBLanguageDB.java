package com.oxipro.cmu.configlang.common.languagedb;

import com.oxipro.cmu.configlang.api.language.Locales;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

import java.util.Locale;
import java.util.UUID;

public class CSSDBLanguageDB implements ILanguageDB {

    private static final String LANG_ISO_KEY = "lang_iso";

    private final PlayerSettingCache playerSettingCache;
    private final PlayerSettingRepository playerSettingRepository;

    public CSSDBLanguageDB(PlayerSettingCache playerSettingCache, PlayerSettingRepository playerSettingRepository) {
        this.playerSettingCache = playerSettingCache;
        this.playerSettingRepository = playerSettingRepository;
    }

    @Override
    public boolean has(UUID playerId) {
        return playerSettingRepository.exists(playerId, LANG_ISO_KEY);
    }

    @Override
    public Locale getLocale(UUID playerId) {
        String raw = playerSettingCache.get(playerId, LANG_ISO_KEY);
        if (raw == null || raw.isBlank()) return null;
        return Locales.parse(raw);
    }

    @Override
    public void setLocale(UUID playerId, Locale locale) {
        playerSettingCache.set(playerId, LANG_ISO_KEY, locale.toString());
    }
}
