package com.oxipro.cmu.configlang.common.langProvider;

import com.oxipro.cmu.configlang.api.language.db.ILanguageDB;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/** Resolves a player locale: DB (optional), then detectors, else null. */
public class PlayerLanguageResolver<T> {

    private final ILanguageDB languageDB;
    private final Function<T, UUID> idExtractor;
    private final List<ILanguageDetector<T>> providers;

    public PlayerLanguageResolver(ILanguageDB languageDB, Function<T, UUID> idExtractor, List<ILanguageDetector<T>> providers) {
        this.languageDB = languageDB;
        this.idExtractor = idExtractor;
        this.providers = providers != null ? List.copyOf(providers) : List.of();
    }

    /** DB preference, then detectors. */
    public Locale resolveLocale(T source, Set<Locale> knownLocales) {
        if (source == null) return null;

        UUID id = idExtractor.apply(source);
        if (id != null && languageDB != null && languageDB.has(id)) {
            return languageDB.getLocale(id);
        }

        return detectLocale(source, knownLocales);
    }

    /** Detectors only (client / IP). No DB. */
    public Locale detectLocale(T source, Set<Locale> knownLocales) {
        if (source == null) return null;

        for (ILanguageDetector<T> provider : providers) {
            Locale locale = provider.detect(source);
            if (locale != null && knownLocales.contains(locale)) {
                return locale;
            }
        }

        return null;
    }
}
