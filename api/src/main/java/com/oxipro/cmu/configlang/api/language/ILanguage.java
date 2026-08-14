package com.oxipro.cmu.configlang.api.language;

import com.oxipro.cmu.configlang.api.config.IConfigFile;

import java.util.List;
import java.util.Locale;

public interface ILanguage {

    Locale getLocale();

    void addDefault(String path, Object value);

    String getFancyName();

    List<String> getMessageAsList(String path);

    String getMessage(String path);

    /** Writes the file. When copyDefaults is true, missing addDefault keys are persisted. */
    void save(boolean copyDefaults);

    IConfigFile getConfig();
}
