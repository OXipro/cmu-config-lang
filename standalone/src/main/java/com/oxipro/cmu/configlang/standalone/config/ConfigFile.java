package com.oxipro.cmu.configlang.standalone.config;

import com.oxipro.cmu.configlang.api.config.IConfigFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigFile implements IConfigFile {

    private final Yaml yaml;
    private final File file;
    private final Map<String, Object> defaults = new LinkedHashMap<>();
    private Map<String, Object> data = new LinkedHashMap<>();

    public ConfigFile(File file) {
        this(file, null);
    }

    public ConfigFile(File file, InputStream defaultResource) {
        this.file = file;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
        createFile(defaultResource);
        load();
    }

    private void createFile(InputStream defaultResource) {
        if (file.exists()) return;
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try {
            if (defaultResource != null) {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    defaultResource.transferTo(out);
                }
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create config file " + file, e);
        }
    }

    @Override
    public void load() {
        try (InputStream in = new FileInputStream(file)) {
            Map<String, Object> loaded = yaml.load(in);
            data = loaded != null ? loaded : new LinkedHashMap<>();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load config file " + file, e);
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public void save() {
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!contains(entry.getKey())) set(entry.getKey(), entry.getValue());
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save config file " + file, e);
        }
    }

    @Override
    public void addDefault(String path, Object value) {
        defaults.put(path, value);
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    @SuppressWarnings("unchecked")
    private void set(String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                next = new LinkedHashMap<String, Object>();
                current.put(parts[i], next);
            }
            current = (Map<String, Object>) next;
        }
        current.put(parts[parts.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private Object get(String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) return null;
            current = (Map<String, Object>) next;
        }
        return current.get(parts[parts.length - 1]);
    }

    @Override
    public String getString(String path) {
        Object value = get(path);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public int getInt(String path) {
        Object value = get(path);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    @Override
    public boolean getBoolean(String path) {
        Object value = get(path);
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @Override
    public long getLong(String path) {
        Object value = get(path);
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    @Override
    public double getDouble(String path) {
        Object value = get(path);
        return value instanceof Number ? ((Number) value).doubleValue() : 0d;
    }

    @Override
    public float getFloat(String path) {
        Object value = get(path);
        return value instanceof Number ? ((Number) value).floatValue() : 0f;
    }

    @Override
    public List<String> getStringList(String path) {
        Object value = get(path);
        if (!(value instanceof List)) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        for (Object element : (List<Object>) value) result.add(String.valueOf(element));
        return result;
    }

    @Override
    public String getFileName() {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
