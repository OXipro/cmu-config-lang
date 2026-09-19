# Config and language

ConfigLang uses the same two-step pattern for **config files** and **language files**.

1. You ship a YAML file in `src/main/resources`. On first run it is copied into the plugin data folder. That copy keeps comments and ordering from your resource.
2. In Java you call `addDefault(path, value)` for every key you care about, then save with copy-defaults. If a key is already in the file, it is left alone. If a plugin update adds a new key, that key is written into the existing file.

That is why you can ship a commented `config.yml`, let server owners edit it, and still inject new options later without wiping their file.

## Config files (Bukkit)

```java
public class DefaultMainConfig extends ConfigFile {
    public DefaultMainConfig(JavaPlugin plugin) {
        super(plugin, "config.yml");
        setDefaults();
        get().options().copyDefaults(true);
        save();
    }

    private void setDefaults() {
        addDefault("feature.enabled", true);
        addDefault("feature.limit", 16);
        addDefault("language.client-locale", true);
        addDefault("language.ip-language", true);
        addDefault("language.fallback", "en_US");
    }
}
```

`new ConfigFile(plugin, "config.yml")` looks for `src/main/resources/config.yml` and copies it to `plugins/YourPlugin/config.yml` if that file does not exist.

Nested paths are fine: `"worldloader.asp.storage"`.

Read values through `IConfigFile`:

```java
IConfigFile config = new DefaultMainConfig(plugin);
boolean enabled = config.getBoolean("feature.enabled");
int limit = config.getInt("feature.limit");
```

Reload:

```java
config.reload();
```

You can have as many files as you want (`cssdb.yml`, `hud.yml`, `sound.yml`, ...). Each one is one `ConfigFile`.

### Language settings in config

These paths are defined in `LanguageSettingsConfigPaths`:

| Key | Meaning |
| --- | --- |
| `language.client-locale` | Use the player's Minecraft language |
| `language.ip-language` | Guess from IP when no other source exists |
| `language.fallback` | Locale used when nothing else matches (`en_US`) |

```java
LanguageSettings settings = LanguageSettings.fromConfig(mainConfig);
```

The fallback locale must be a language you actually registered.

## Language files

Put one YAML per locale in resources:

```
src/main/resources/languages/en_US.yml
src/main/resources/languages/fr_FR.yml
```

File name = locale (`en_US`, `fr_FR`). Extra files dropped in `plugins/YourPlugin/languages/` at runtime are loaded automatically.

Subclass `Language` for the locales you ship, so updates can add missing strings:

```java
public class English extends Language {
    public English(JavaPlugin plugin) {
        super(new ConfigFile(plugin, "languages/en_US.yml"));
    }

    @Override
    protected void saveDefaults() {
        addDefault("fancy-name", "English");
        addDefault("welcome", "Hello {player}");
        addDefault("error.no-permission", "You cannot do that.");
        addDefault("help.lines", java.util.List.of(
                "Line 1",
                "Line 2"
        ));
        save(true);
    }
}
```

Register them when you init ConfigLang:

```java
Map<Locale, ILanguage> defaults = new HashMap<>();
defaults.put(Locale.US, new English(plugin));
defaults.put(Locale.FRANCE, new French(plugin));
configLang.init(defaults);
```

### Reading messages

```java
ILanguage lang = languageManager.getPlayerLanguage(player);
if (lang.has("welcome")) {
    String text = lang.getMessage("welcome");
}
List<String> lines = lang.getMessageAsList("help.lines");
```

`has(path)` is a map lookup on the language file (and defaults). If a path is missing, `getMessage` returns a `#config-lang: 'path' is not set` marker so you notice it in-game. Prefer `has` when a missing key is a normal fallback, not an error.

`fancy-name` is the display name of the language (`lang.getFancyName()`).

### Changing a player's language

Stored in CSSDB under `lang_iso`. See [CSSDB](cssdb.md).

```java
languageManager.setPlayerLanguage(player.getUniqueId(), Locale.FRANCE);
ILanguage again = languageManager.getPlayerLanguage(player.getUniqueId());
```

On Bukkit / Velocity / Minestom you can also pass the platform player:

```java
ILanguage lang = languageManager.getPlayerLanguage(player);
```

That uses DB, then client locale, then IP, then fallback.

`detectPlayerLanguage(player)` skips the DB.

## Standalone / Velocity / Minestom config

Bukkit `ConfigFile` uses the server YAML API. Other platforms use SnakeYAML:

```java
import com.oxipro.cmu.configlang.standalone.config.ConfigFile;

File file = new File(dataFolder, "config.yml");
ConfigFile config = new ConfigFile(file, getClass().getResourceAsStream("/config.yml"));
config.addDefault("language.fallback", "en_US");
config.save(true);
```

Standalone `Language` lives in `com.oxipro.cmu.configlang.standalone.language.Language`. Velocity and Minestom reuse it.

Standalone player type is `PlayerContext(UUID id, String ip)`:

```java
ILanguage lang = configLang.getPlayerLanguage(new PlayerContext(uuid, ip));
```

## Paths as constants

Keep YAML keys in a constants class so config and language stay in sync:

```java
public final class LangPaths {
    public static final String WELCOME = "welcome";
    public static final String NO_PERMISSION = "error.no-permission";
    private LangPaths() {}
}
```

Then `addDefault(LangPaths.WELCOME, "Hello {player}")` and `lang.getMessage(LangPaths.WELCOME)`.
