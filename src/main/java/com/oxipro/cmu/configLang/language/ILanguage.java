package com.oxipro.cmu.configLang.language;

import com.oxipro.cmu.configLang.config.ConfigFile;

public interface ILanguage {

    String getId();

    void addDefault(String path, Object value);

    String getFancyName();

    String getMessage(String path);

    void save(boolean copyDefaults);

    ConfigFile getConfig();
}
