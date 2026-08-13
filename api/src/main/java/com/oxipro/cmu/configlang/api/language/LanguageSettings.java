package com.oxipro.cmu.configlang.api.language;

import com.oxipro.cmu.configlang.api.language.defaults.DefaultValues;

import java.util.Locale;
import java.util.Objects;

/** Detection options: client locale, IP locale, fallback. */
public final class LanguageSettings {

    private final boolean clientLocaleEnabled;
    private final boolean ipLanguageEnabled;
    private final Locale fallbackLocale;

    private LanguageSettings(boolean clientLocaleEnabled, boolean ipLanguageEnabled, Locale fallbackLocale) {
        this.clientLocaleEnabled = clientLocaleEnabled;
        this.ipLanguageEnabled = ipLanguageEnabled;
        this.fallbackLocale = Objects.requireNonNull(fallbackLocale, "fallbackLocale");
    }

    public static LanguageSettings defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isClientLocaleEnabled() {
        return clientLocaleEnabled;
    }

    public boolean isIpLanguageEnabled() {
        return ipLanguageEnabled;
    }

    public Locale getFallbackLocale() {
        return fallbackLocale;
    }

    public LanguageSettings withFallbackLocale(Locale fallbackLocale) {
        return new LanguageSettings(clientLocaleEnabled, ipLanguageEnabled, fallbackLocale);
    }

    public LanguageSettings withClientLocaleEnabled(boolean clientLocaleEnabled) {
        return new LanguageSettings(clientLocaleEnabled, ipLanguageEnabled, fallbackLocale);
    }

    public LanguageSettings withIpLanguageEnabled(boolean ipLanguageEnabled) {
        return new LanguageSettings(clientLocaleEnabled, ipLanguageEnabled, fallbackLocale);
    }

    public static final class Builder {
        private boolean clientLocaleEnabled = true;
        private boolean ipLanguageEnabled = false;
        private Locale fallbackLocale = DefaultValues.FALLBACK_LOCALE;

        public Builder clientLocale(boolean enabled) {
            this.clientLocaleEnabled = enabled;
            return this;
        }

        public Builder ipLanguage(boolean enabled) {
            this.ipLanguageEnabled = enabled;
            return this;
        }

        public Builder fallbackLocale(Locale fallbackLocale) {
            this.fallbackLocale = fallbackLocale;
            return this;
        }

        public LanguageSettings build() {
            return new LanguageSettings(clientLocaleEnabled, ipLanguageEnabled, fallbackLocale);
        }
    }
}
