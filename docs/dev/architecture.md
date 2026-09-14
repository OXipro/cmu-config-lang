# Architecture (maintainers)

This is for people changing ConfigLang itself. Plugin authors should start at [Getting started](../user/getting-started.md).

## Modules

```
api          interfaces (IConfigFile, ILanguage, ILanguageManager, ILanguageDB, LanguageSettings)
common       CSSDBLanguageDB, IP detector, PlayerLanguageResolver
bukkit       Bukkit ConfigFile + Language + LanguageManager + ConfigLang
standalone   SnakeYAML ConfigFile + Language + LanguageManager + ConfigLang + PlayerContext
velocity     ConfigLang + LanguageManager (extends standalone)
minestom     ConfigLang + LanguageManager (extends standalone)
```

Parent POM: `com.oxipro.cmu.configlang:cmu-config-lang:1.0`.

Java:

- 11: `api`, `common`, `bukkit`
- 17: `velocity`
- 21: `standalone`, `minestom`

Bukkit is compiled against Spigot 1.8.8. Client locale uses reflection so modern `Player#getLocale` and legacy `Player.spigot().getLocale()` both work.

## Package layout

Everything lives under `com.oxipro.cmu.configlang`. Downstream plugins relocate `com.oxipro.cmu`. Do not put classes outside that tree.

CSSDB is a **separate** library (`com.oxipro.cssdb`). ConfigLang `common` depends on `cssdb-api`. CSSDB `cssdb-api` depends on ConfigLang `api` (`IConfigFile` for `DBConfig.fromConfigFile`). Keep that split.

## Config copy flow

Bukkit `ConfigFile`:

1. `new File(plugin.getDataFolder(), path)`
2. If missing, `plugin.saveResource(path, false)` (byte copy of the resource, comments kept)
3. `YamlConfiguration.loadConfiguration(file)`
4. Caller `addDefault` + `options().copyDefaults(true)` + `save()`

Standalone `ConfigFile`:

1. If the file is missing and an `InputStream` was passed, copy the stream
2. Parse with SnakeYAML
3. `addDefault` stores in a separate map
4. `save(true)` writes keys that are not in the file yet

Bukkit `YamlConfiguration.save()` can drop comments on later saves. The first copy still ships a commented file. Prefer putting new keys in `addDefault` rather than rewriting the whole resource over the data folder.

## Language load order

`LanguageManager.loadLanguages`:

1. Register the `Map<Locale, ILanguage>` passed to `init` (your `English` / `French` subclasses, which run `saveDefaults`)
2. Scan `languages/*.yml` (and `*.yaml`) in the data folder
3. Skip a file if that locale is already registered

Locale from filename: `Locales.parse("en_US")`.

Default fallback in `DefaultValues.FALLBACK_LOCALE` is `en_EN`. Callers should set fallback from config (`LanguageSettings.fromConfig`) to a locale they actually registered, usually `en_US`.

## Player locale resolution

`PlayerLanguageResolver`:

1. If CSSDB `has(uuid)` for `lang_iso`, use that
2. Else each `ILanguageDetector` in order (client locale, then IP)
3. Detector results are kept only if the locale is in the loaded set
4. Null from the resolver becomes the manager fallback

`getPlayerLanguage(UUID)` is DB-only (or fallback).  
`getPlayerLanguage(Player)` is the full chain.  
`detectPlayerLanguage(Player)` is detectors only.

CSSDB key is hard-coded in `CSSDBLanguageDB`:

```java
private static final String LANG_ISO_KEY = "lang_iso";
```

`setLocale` writes the cache and `save`s immediately.

## Platform ConfigLang constructors

All platforms take CSSDB `PlayerSettingCache` + `PlayerSettingRepository` (or you could pass another `ILanguageDB` if you change the managers; the public `ConfigLang` wrappers currently always build `CSSDBLanguageDB`).

| Platform | Extra argument |
| --- | --- |
| Bukkit | `JavaPlugin` |
| Velocity / Minestom / standalone | `File dataFolder` |

Standalone has no client-locale detector. Only IP + DB + fallback. Use `PlayerContext(id, ip)`.

## Adding a platform

1. New module depending on `api` + either `bukkit` style or `standalone`
2. Implement or reuse `ConfigFile`
3. `Language` subclass or reuse standalone `Language`
4. `LanguageManager` with a `PlayerLanguageResolver` and platform-specific `ILanguageDetector`
5. `ConfigLang` wrapper matching the existing constructors
6. Document the artifact in the root README and in `docs/user/maven.md` / `gradle.md`

## Build

```bash
mvn install
```

No tests in this repo yet. After an API change, build CME (or another consumer) against `mavenLocal()` before tagging.

## License

AGPL-3.0. Downstream plugins that shade this library need to comply with that license.
