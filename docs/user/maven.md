# Maven

Install this repo first so the artifacts exist in `~/.m2`:

```bash
mvn install
```

Do the same for [CSSDB](https://github.com/OXipro/cssdb-api).

There is no public Maven repo for ConfigLang yet. Depend on the local install.

## Dependencies

Use **one** platform artifact.

### Bukkit / Spigot / Paper (1.8+)

```xml
<dependencies>
  <dependency>
    <groupId>com.oxipro.cmu.configlang</groupId>
    <artifactId>bukkit</artifactId>
    <version>1.0</version>
  </dependency>
  <dependency>
    <groupId>com.oxipro.cssdb</groupId>
    <artifactId>cssdb-api</artifactId>
    <version>1.0</version>
  </dependency>
  <!-- JDBC driver for the CSSDB provider you use (MySQL example) -->
  <dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.3.0</version>
  </dependency>
</dependencies>
```

For PostgreSQL, add `org.postgresql:postgresql` instead (or as well). CSSDB marks both connectors as `provided`, so you must bring the one you need.

### Velocity

```xml
<dependency>
  <groupId>com.oxipro.cmu.configlang</groupId>
  <artifactId>velocity</artifactId>
  <version>1.0</version>
</dependency>
```

### Minestom

```xml
<dependency>
  <groupId>com.oxipro.cmu.configlang</groupId>
  <artifactId>minestom</artifactId>
  <version>1.0</version>
</dependency>
```

### Standalone

```xml
<dependency>
  <groupId>com.oxipro.cmu.configlang</groupId>
  <artifactId>standalone</artifactId>
  <version>1.0</version>
</dependency>
```

## Relocate (required)

If two plugins both shade ConfigLang without relocating, their classes collide. Relocate the whole `com.oxipro.cmu` package into **your** plugin.

Recommended pattern:

```
com.oxipro.cmu   -> my.plugin.example.libs.cmu
com.oxipro.cssdb -> my.plugin.example.libs.cssdb
```

Also relocate Gson and HikariCP (they come in transitively).

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.5.3</version>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
          <configuration>
            <relocations>
              <relocation>
                <pattern>com.oxipro.cmu</pattern>
                <shadedPattern>my.plugin.example.libs.cmu</shadedPattern>
              </relocation>
              <relocation>
                <pattern>com.oxipro.cssdb</pattern>
                <shadedPattern>my.plugin.example.libs.cssdb</shadedPattern>
              </relocation>
              <relocation>
                <pattern>com.google.gson</pattern>
                <shadedPattern>my.plugin.example.libs.gson</shadedPattern>
              </relocation>
              <relocation>
                <pattern>com.zaxxer.hikari</pattern>
                <shadedPattern>my.plugin.example.libs.hikari</shadedPattern>
              </relocation>
              <!-- standalone / velocity / minestom only -->
              <relocation>
                <pattern>org.yaml.snakeyaml</pattern>
                <shadedPattern>my.plugin.example.libs.snakeyaml</shadedPattern>
              </relocation>
            </relocations>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Replace `my.plugin.example` with your plugin's root package.

Your Java source still imports `com.oxipro.cmu.configlang...`. Shade rewrites the bytecode. Do not import the relocated package in source.

## What not to shade

Spigot / Paper / Velocity / Minestom APIs (`provided`).
