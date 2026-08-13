package com.oxipro.cmu.configlang.api.language.detection;

import java.util.Locale;

public interface ILanguageDetector<T> {
    Locale detect(T source);
}
