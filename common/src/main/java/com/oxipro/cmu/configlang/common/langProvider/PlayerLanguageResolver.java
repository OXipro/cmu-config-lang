package com.oxipro.cmu.configlang.common.langProvider;

import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class PlayerLanguageResolver<T> {

    private final ILanguageDB languageDB;
    private final Function<T, UUID> idExtractor;
    private final ChainedLanguageProvider<T> chain;

    public PlayerLanguageResolver(ILanguageDB languageDB, Function<T, UUID> idExtractor, List<ILanguageDetector<T>> providers) {
        this.languageDB = languageDB;
        this.idExtractor = idExtractor;
        this.chain = new ChainedLanguageProvider<>(providers);
    }

    public String resolveLangId(T source, Set<String> knownLanguageIds) {
        UUID id = idExtractor.apply(source);

        if (languageDB.has(id)) {
            return languageDB.getLangId(id);
        }

        Locale locale = chain.detect(source);
        return knownLanguageIds.contains(locale.getLanguage()) ? locale.getLanguage() : null;
    }
}
