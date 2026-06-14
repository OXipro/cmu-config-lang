package com.oxipro.cmu.configLang.language;

import com.oxipro.cmu.configLang.config.ConfigFile;

import java.util.List;

public interface ILanguage {

    String getId();

    void addDefault(String path, Object value);

    String getFancyName();

    List<String> getMessageAsList(String path);

    String getMessage(String path);

    void save(boolean copyDefaults);

    ConfigFile getConfig();
}
