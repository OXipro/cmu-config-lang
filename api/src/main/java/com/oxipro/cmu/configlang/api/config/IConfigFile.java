package com.oxipro.cmu.configlang.api.config;

import java.util.List;

public interface IConfigFile {
    void load();

    void reload();

    void save();

    /** Fallback used by getters and {@link #contains} when the path is absent from the file. */
    void addDefault(String path, Object value);

    /** True if the path exists in the file or as a default. */
    boolean contains(String path);

    String getString(String path);

    int getInt(String path);

    boolean getBoolean(String path);

    long getLong(String path);

    double getDouble(String path);

    float getFloat(String path);

    List<String> getStringList(String path);

    String getFileName();
}