# CSSDB

Player language is not stored in a YAML file. ConfigLang writes it to [CSSDB](https://github.com/OXipro/cssdb-api) player settings, key `lang_iso`.

CSSDB is a small stats + settings database. ConfigLang only needs the player-settings table.

Supported providers **right now**:

- MySQL (MariaDB Java Client works)
- PostgreSQL

The plugin jar can get large because of all the libraries this stack supports (CSSDB, HikariCP, Gson, JDBC connectors, ...). (You can download some of those jars at runtime with Libby or Maven instead. That is optional.)

## CSSDB config

`src/main/resources/cssdb.yml`:

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

PostgreSQL:

```yaml
database:
  provider: postgresql
  host: "localhost"
  port: 5432
  database: "cssdb"
  username: "cssdb"
  password: "password"
  jdbcUrl: "jdbc:postgresql://{host}:{port}/{database}"
```

`DBConfig.fromConfigFile` reads the `database.*` keys. `{host}`, `{port}`, `{database}` in `jdbcUrl` are replaced automatically.

## Start CSSDB, then ConfigLang

The JDBC driver for the provider you picked must be on the classpath before `cssdb.init()`.

```java
IConfigFile cssdbFile = new ConfigFile(plugin, "cssdb.yml");
DBConfig cssdbConfig = DBConfig.fromConfigFile(cssdbFile);

CSSDB cssdb = new CSSDB(DatabaseFactory.create(cssdbConfig));
cssdb.init();
cssdb.getPlayerSettingsRepository().initTable();

ConfigLang configLang = new ConfigLang(
        plugin,
        cssdb.getPlayerSettingCache(),
        cssdb.getPlayerSettingsRepository(),
        LanguageSettings.fromConfig(mainConfig)
);
```

On join, load the player into the CSSDB cache so language reads are cheap:

```java
cssdb.getPlayerSettingCache().load(player.getUniqueId());
```

On quit:

```java
cssdb.getPlayerSettingCache().unload(player.getUniqueId());
```

`setPlayerLanguage` already writes through the cache and saves.

## What ConfigLang stores

| CSSDB key | Value |
| --- | --- |
| `lang_iso` | Locale string, for example `en_US` |

You can store other player settings in the same table. They will not conflict as long as you do not reuse `lang_iso`.
