# Getting started

This page is the shortest path to a working Bukkit plugin. Velocity and Minestom follow the same idea, only the `ConfigLang` class package and `ConfigFile` constructor change.

A real plugin that uses this library is [CME](https://github.com/OXipro/CME). The snippets here are a smaller copy of that setup.

## What you need

1. Install ConfigLang: `mvn install` in this repo.
2. Install [CSSDB](https://github.com/OXipro/cssdb-api) the same way (`mvn install`).
3. Add the platform artifact + CSSDB (see [Maven](maven.md) or [Gradle](gradle.md)).
4. Relocate `com.oxipro.cmu` (and CSSDB).
5. Ship YAML files in `src/main/resources`.
6. Start CSSDB, then `ConfigLang`.

## Resource files

Put the YAML **in your plugin resources**. On first run ConfigLang copies them into the plugin data folder, comments included. On later plugin versions, `addDefault` + save fills keys that were missing.

```
src/main/resources/
  config.yml
  cssdb.yml
  languages/
    en_US.yml
```

`config.yml`:

```yaml
# Example plugin config
# Comments in this file are kept on the first copy to the data folder.

language:
  client-locale: true
  ip-language: true
  fallback: en_US
```

`cssdb.yml`:

```yaml
# CSSDB Custom Stats and Settings DataBase
database:
  provider: mysql
  host: "localhost"
  port: 3306
  database: "cssdb"
  username: "cssdb"
  password: "password"
  jdbcUrl: "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true&allowMultiQueries=true&useSSL=false"
```

`languages/en_US.yml`:

```yaml
fancy-name: "English"
welcome: "Hello {player}"
```

The language file name **is** the locale: `en_US.yml`, `fr_FR.yml`, ...

## Default config class

Subclass `ConfigFile`, call `addDefault` for every key, then copy defaults and save. Existing values in the player's file are never overwritten. New keys from a plugin update are appended.

```java
package my.plugin.example.configuration;

import com.oxipro.cmu.configlang.api.config.defaults.LanguageSettingsConfigPaths;
import com.oxipro.cmu.configlang.bukkit.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultMainConfig extends ConfigFile {

    public DefaultMainConfig(JavaPlugin plugin) {
        super(plugin, "config.yml");
        setDefaults();
        get().options().copyDefaults(true);
        save();
    }

    private void setDefaults() {
        addDefault(LanguageSettingsConfigPaths.LANGUAGE_CLIENT_LOCALE, true);
        addDefault(LanguageSettingsConfigPaths.LANGUAGE_IP, true);
        addDefault(LanguageSettingsConfigPaths.LANGUAGE_FALLBACK, "en_US");
    }
}
```

## Default language class

Subclass `Language`, put strings in `saveDefaults()`, then `save(true)`.

```java
package my.plugin.example.language;

import com.oxipro.cmu.configlang.bukkit.config.ConfigFile;
import com.oxipro.cmu.configlang.bukkit.language.Language;
import org.bukkit.plugin.java.JavaPlugin;

public class English extends Language {

    public English(JavaPlugin plugin) {
        super(new ConfigFile(plugin, "languages/en_US.yml"));
    }

    @Override
    protected void saveDefaults() {
        addDefault("fancy-name", "English");
        addDefault("welcome", "Hello {player}");
        save(true);
    }
}
```

## Plugin enable order

1. Connect CSSDB and init the player-settings table.
2. Build `ConfigLang` with the CSSDB cache + repository.
3. Register default languages and call `init`.

```java
package my.plugin.example;

import my.plugin.example.configuration.DefaultMainConfig;
import my.plugin.example.language.English;
import com.oxipro.cmu.configlang.api.config.IConfigFile;
import com.oxipro.cmu.configlang.api.language.ILanguage;
import com.oxipro.cmu.configlang.api.language.LanguageSettings;
import com.oxipro.cmu.configlang.bukkit.ConfigLang;
import com.oxipro.cmu.configlang.bukkit.config.ConfigFile;
import com.oxipro.cmu.configlang.bukkit.language.LanguageManager;
import com.oxipro.cssdb.CSSDB;
import com.oxipro.cssdb.config.DBConfig;
import com.oxipro.cssdb.database.DatabaseFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ExamplePlugin extends JavaPlugin {

    private IConfigFile mainConfig;
    private CSSDB cssdb;
    private ConfigLang configLang;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        mainConfig = new DefaultMainConfig(this);
        IConfigFile cssdbFile = new ConfigFile(this, "cssdb.yml");

        DBConfig cssdbConfig = DBConfig.fromConfigFile(cssdbFile);
        cssdb = new CSSDB(DatabaseFactory.create(cssdbConfig));
        cssdb.init();
        cssdb.getPlayerSettingsRepository().initTable();

        configLang = new ConfigLang(
                this,
                cssdb.getPlayerSettingCache(),
                cssdb.getPlayerSettingsRepository(),
                LanguageSettings.fromConfig(mainConfig)
        );

        Map<Locale, ILanguage> defaults = new HashMap<>();
        defaults.put(Locale.US, new English(this));
        configLang.init(defaults);
        languageManager = configLang.getLanguageManager();
    }

    @Override
    public void onDisable() {
        if (cssdb != null) {
            cssdb.disconnect();
        }
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public void sendWelcome(Player player) {
        ILanguage lang = languageManager.getPlayerLanguage(player);
        player.sendMessage(lang.getMessage("welcome").replace("{player}", player.getName()));
    }
}
```

## How a player's language is chosen

1. CSSDB setting `lang_iso` if the player already picked one
2. Client locale (Minecraft language), if `language.client-locale` is true
3. IP-based guess, if `language.ip-language` is true
4. Fallback locale (`language.fallback`, must match a loaded language)

To force a language:

```java
languageManager.setPlayerLanguage(player.getUniqueId(), Locale.US);
```

To ignore the DB (for example cracked offline guests):

```java
ILanguage detected = languageManager.detectPlayerLanguage(player);
```

## Other platforms

| Platform | ConfigLang class | Config file |
| --- | --- | --- |
| Bukkit | `com.oxipro.cmu.configlang.bukkit.ConfigLang` | `new ConfigFile(plugin, "config.yml")` |
| Velocity | `com.oxipro.cmu.configlang.velocity.ConfigLang` | standalone `ConfigFile(file, resourceStream)` |
| Minestom | `com.oxipro.cmu.configlang.minestom.ConfigLang` | same as Velocity |
| No server | `com.oxipro.cmu.configlang.standalone.ConfigLang` | same, plus `PlayerContext(uuid, ip)` |

Velocity / Minestom / standalone take a `File dataFolder` instead of a `JavaPlugin`. Copy the resource yourself into that folder (or pass the resource `InputStream` to standalone `ConfigFile`).

```java
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

File file = new File(dataFolder, "config.yml");
ConfigFile config = new ConfigFile(file, getClass().getResourceAsStream("/config.yml"));
config.addDefault("language.fallback", "en_US");
config.save(true);
```

Then:

```java
new com.oxipro.cmu.configlang.velocity.ConfigLang(
        dataFolder,
        cssdb.getPlayerSettingCache(),
        cssdb.getPlayerSettingsRepository(),
        LanguageSettings.fromConfig(config)
);
```
