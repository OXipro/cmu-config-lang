# CMU ConfigLang

**CMU** (Custom Minecraft Utilities) is a set of libraries for Minecraft plugins.

**ConfigLang** is the CMU library for:

- YAML config files with comments kept on first copy, and missing keys filled on default
- per-player language files, the same API on Bukkit 1.8, Velocity, Minestom, and standalone

It is a **library**, not a plugin. You shade it into your plugin. If several plugins on the same server use ConfigLang, you **must relocate** it.

## Platforms

| Module | Artifact | Java | Notes |
| --- | --- | --- | --- |
| API | `com.oxipro.cmu.configlang:api` | 11 | Interfaces only |
| Common | `com.oxipro.cmu.configlang:common` | 11 | CSSDB language store, IP detection |
| Bukkit | `com.oxipro.cmu.configlang:bukkit` | 11 | Spigot/Paper from 1.8.8 |
| Standalone | `com.oxipro.cmu.configlang:standalone` | 21 | SnakeYAML, no server API |
| Velocity | `com.oxipro.cmu.configlang:velocity` | 17 | Uses standalone config |
| Minestom | `com.oxipro.cmu.configlang:minestom` | 21 | Uses standalone config |

Pick **one** platform artifact. It already pulls `api` and `common` (and `standalone` for Velocity / Minestom).

## Relocate

Default recommendation: relocate the whole `com.oxipro.cmu` tree into your plugin package.

```
com.oxipro.cmu  ->  my.plugin.example.libs.cmu
```

Do the same for CSSDB. Full Maven and Gradle examples:

- [Maven](docs/user/maven.md)
- [Gradle](docs/user/gradle.md)

## Player language storage

Chosen locale is stored as a CSSDB player setting (`lang_iso`).

CSSDB currently supports **MySQL** (MariaDB driver is fine) and **PostgreSQL**.

- CSSDB: https://github.com/OXipro/cssdb-api
- How to wire it: [CSSDB](docs/user/cssdb.md)

The shaded plugin jar can get large because of all the libraries this stack supports. (You can download some of them at runtime with Libby or Maven instead. That is optional.)

## Docs

**Plugin authors**

- [Getting started](docs/user/getting-started.md)
- [Maven](docs/user/maven.md)
- [Gradle](docs/user/gradle.md)
- [Config and language](docs/user/config-and-language.md)
- [CSSDB](docs/user/cssdb.md)

**Library maintainers**

- [Architecture](docs/dev/architecture.md)

## Build this repo

```bash
mvn install
```

That installs the modules into your local Maven repository so other projects can depend on them.

## License

[MIT License](LICENSE.md)
