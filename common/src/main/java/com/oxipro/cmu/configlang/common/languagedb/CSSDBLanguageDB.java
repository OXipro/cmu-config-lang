package com.oxipro.cmu.configlang.common.languagedb;

import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cssdb.cache.PlayerSettingCache;
import com.oxipro.cssdb.repository.playerSettings.PlayerSettingRepository;

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
    public String getLangId(UUID playerId) {
        return playerSettingCache.get(playerId, LANG_ISO_KEY);
    }

    @Override
    public void setLangId(UUID playerId, String langId) {
        playerSettingCache.set(playerId, LANG_ISO_KEY, langId);
    }
}
