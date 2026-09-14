# Gradle

Install this repo first so the artifacts exist in `~/.m2`:

```bash
mvn install
```

Do the same for [CSSDB](https://github.com/OXipro/cssdb-api).

Then point Gradle at `mavenLocal()`.

## Kotlin DSL (`build.gradle.kts`)

```kotlin
plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11)) // 17 for Velocity, 21 for Minestom
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("com.oxipro.cmu.configlang:bukkit:1.0")
    implementation("com.oxipro.cssdb:cssdb-api:1.0")
    implementation("com.mysql:mysql-connector-j:9.3.0") // JDBC driver for the CSSDB provider you use
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("com.oxipro.cmu", "my.plugin.example.libs.cmu")
    relocate("com.oxipro.cssdb", "my.plugin.example.libs.cssdb")
    relocate("com.google.gson", "my.plugin.example.libs.gson")
    relocate("com.zaxxer.hikari", "my.plugin.example.libs.hikari")
    // standalone / velocity / minestom:
    // relocate("org.yaml.snakeyaml", "my.plugin.example.libs.snakeyaml")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
```

Replace `my.plugin.example` with your plugin's root package.

For PostgreSQL, add `org.postgresql:postgresql` instead (or as well). CSSDB marks both connectors as `provided`, so you must bring the one you need.

## Groovy (`build.gradle`)

```groovy
plugins {
    id 'java'
    id 'com.gradleup.shadow' version '8.3.6'
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/' }
}

dependencies {
    compileOnly 'org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT'
    implementation 'com.oxipro.cmu.configlang:bukkit:1.0'
    implementation 'com.oxipro.cssdb:cssdb-api:1.0'
    implementation 'com.mysql:mysql-connector-j:9.3.0'
}

shadowJar {
    archiveClassifier.set('')
    relocate 'com.oxipro.cmu', 'my.plugin.example.libs.cmu'
    relocate 'com.oxipro.cssdb', 'my.plugin.example.libs.cssdb'
    relocate 'com.google.gson', 'my.plugin.example.libs.gson'
    relocate 'com.zaxxer.hikari', 'my.plugin.example.libs.hikari'
}
```

## Other platforms

| Platform | ConfigLang artifact |
| --- | --- |
| Bukkit | `com.oxipro.cmu.configlang:bukkit:1.0` |
| Velocity | `com.oxipro.cmu.configlang:velocity:1.0` |
| Minestom | `com.oxipro.cmu.configlang:minestom:1.0` |
| Standalone | `com.oxipro.cmu.configlang:standalone:1.0` |

## Relocate

Always relocate `com.oxipro.cmu` when more than one plugin on the server may shade ConfigLang.

Your Java source still imports `com.oxipro.cmu.configlang...`. Shadow rewrites the bytecode. Do not import the relocated package in source.
