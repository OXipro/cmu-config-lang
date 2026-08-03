package com.oxipro.cmu.configlang.standalone.language;

import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.ILanguageManager;
import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.defaults.DefaultValues;
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LanguageManager implements ILanguageManager {

    private final Map<String, ILanguage> languages = new HashMap<>();
    private final File languagesFolder;
    private final ILanguageDB languageDB;

    public LanguageManager(File languagesFolder, Map<String, ILanguage> defaultLanguages, ILanguageDB languageDB) {
        this.languagesFolder = languagesFolder;
        this.languageDB = languageDB;
        loadLanguages(defaultLanguages);
    }

    private void loadLanguages(Map<String, ILanguage> defaultLanguages) {
        if (defaultLanguages != null) {
            for (Map.Entry<String, ILanguage> entry : defaultLanguages.entrySet()) {
                addLanguage(entry.getKey(), entry.getValue());
            }
        }

        if (!languagesFolder.exists()) languagesFolder.mkdirs();

        File[] files = languagesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().replace(".yml", "").toLowerCase(Locale.ROOT);
            if (languages.containsKey(id)) continue;
            addLanguage(id, new Language(new ConfigFile(file)));
        }
    }

    @Override
    public void addLanguage(String id, ILanguage language) {
        languages.put(id.toLowerCase(Locale.ROOT), language);
    }

    @Override
    public ILanguage getLanguage(String id) {
        ILanguage fallback = languages.get(DefaultValues.FALLBACK_LANGUAGE_ID);
        if (id == null) return fallback;
        return languages.getOrDefault(id.toLowerCase(Locale.ROOT), fallback);
    }

    public boolean hasLanguage(String id) {
        return id != null && languages.containsKey(id.toLowerCase(Locale.ROOT));
    }

    public Set<String> getLanguageIds() {
        return languages.keySet();
    }

    @Override
    public ILanguage getPlayerLanguage(UUID playerId) {
        if (playerId == null) return null;
        if (languageDB != null && languageDB.has(playerId)) {
            return getLanguage(languageDB.getLangId(playerId));
        }
        return getLanguage(null);
    }

    public void setPlayerLanguage(UUID playerId, String langId) {
        if (languageDB != null) languageDB.setLangId(playerId, langId);
    }
}
