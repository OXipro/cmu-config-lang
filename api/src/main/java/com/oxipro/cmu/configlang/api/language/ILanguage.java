package com.oxipro.cmu.configlang.api.language;

import com.oxipro.cmu.configlang.api.config.IConfigFile;

import java.util.List;

public interface ILanguage {

    String getId();

    void addDefault(String path, Object value);

    String getFancyName();

    List<String> getMessageAsList(String path);

    String getMessage(String path);

    void save(boolean copyDefaults);

    IConfigFile getConfig();
}
