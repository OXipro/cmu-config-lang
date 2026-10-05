package com.oxipro.cmu.configlang.api;

import com.oxipro.cmu.configlang.api.language.ILanguageManager;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;

public interface IConfigLang {
    ILanguageManager getLanguageManager();

    LanguageSettings getSettings();
}
