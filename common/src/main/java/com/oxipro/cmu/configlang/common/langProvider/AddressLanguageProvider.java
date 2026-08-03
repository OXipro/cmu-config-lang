package com.oxipro.cmu.configlang.common.langProvider;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;

import java.util.Locale;
import java.util.function.Function;

public class AddressLanguageProvider<T> implements ILanguageDetector<T> {

    private final IpLanguageDetector ipLanguageDetector;
    private final Function<T, String> addressExtractor;

    public AddressLanguageProvider(IpLanguageDetector ipLanguageDetector, Function<T, String> addressExtractor) {
        this.ipLanguageDetector = ipLanguageDetector;
        this.addressExtractor = addressExtractor;
    }

    @Override
    public Locale detect(T source) {
        return ipLanguageDetector.detect(addressExtractor.apply(source));
    }
}
