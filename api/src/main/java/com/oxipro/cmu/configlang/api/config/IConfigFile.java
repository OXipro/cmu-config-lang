package com.oxipro.cmu.configlang.api.config;

import java.util.List;

public interface IConfigFile {
    void load();

    void reload();

    void save();

    void addDefault(String path, Object value);

    String getString(String path);

    int getInt(String path);

    boolean getBoolean(String path);

    long getLong(String path);

    double getDouble(String path);

    float getFloat(String path);

    List<String> getStringList(String path);

    String getFileName();
}