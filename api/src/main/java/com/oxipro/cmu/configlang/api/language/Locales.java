package com.oxipro.cmu.configlang.api.language;

import java.util.Locale;
import java.util.Objects;

/** Parses {@code language_COUNTRY} strings from language files (e.g. {@code en_US.yml}). */
public final class Locales {

    private Locales() {
    }

    public static Locale parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        String s = raw.trim().replace('-', '_');
        if (s.isEmpty()) throw new IllegalArgumentException("empty locale");

        int i = s.indexOf('_');
        if (i < 0) return new Locale(s);
        return new Locale(s.substring(0, i), s.substring(i + 1));
    }
}
