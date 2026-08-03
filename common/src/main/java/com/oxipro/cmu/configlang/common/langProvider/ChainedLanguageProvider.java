package com.oxipro.cmu.configlang.common.langProvider;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;

import java.util.List;
import java.util.Locale;

public class ChainedLanguageProvider<T> implements ILanguageDetector<T> {

    private final List<ILanguageDetector<T>> providers;

    public ChainedLanguageProvider(List<ILanguageDetector<T>> providers) {
        this.providers = providers;
    }

    @Override
    public Locale detect(T source) {
        for (ILanguageDetector<T> provider : providers) {
            Locale locale = provider.detect(source);
            if (locale != null) return locale;
        }
        return Locale.ENGLISH;
    }
}
