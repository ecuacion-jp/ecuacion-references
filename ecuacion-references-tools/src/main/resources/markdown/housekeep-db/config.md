This page covers the two config files used by `ecuacion-tool-housekeep-db` — `application.properties` and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a housekeep-db-specific mechanism — it's a Spring Boot feature. `application.yml` / `application.yaml` work exactly the same way. See [Spring Boot's own reference](https://docs.spring.io/spring-boot/reference/features/external-config.html) for the full picture. For where to place this file, see [File Placement](#file-placement) below.

### Available Settings

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `jp.ecuacion.tool.housekeep-db.excel-path` | ○ | — | Path to the excel configuration file to run |
| `jp.ecuacion.tool.housekeep-db.max-select-lines` | — | `1000` | The number of rows the main SELECT retrieves and commits per loop iteration |

```properties
jp.ecuacion.tool.housekeep-db.excel-path=/path/to/your-settings.xlsx
jp.ecuacion.tool.housekeep-db.max-select-lines=1000
```

---

## logback-spring.xml

The Logback configuration file; placed as described in [File Placement](#file-placement) below.

### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="log-dir" value="/path/to/logs/directory" />
    <property name="loglevel-spring" value="INFO" />
    <include resource="logback-spring-appenders.xml" />
    <include resource="logback-spring-appenders-local.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-for-local.xml" />
</configuration>
```

---

## File Placement

Placement for `application.properties` is standard Spring Boot externalized-configuration behavior — nothing housekeep-db-specific. `logback-spring.xml` is *almost* the same, except Spring Boot doesn't provide the equivalent lookup on its own, so `ecuacion-splib-core` adds it. Both follow the same three-tier lookup: an explicit path via a system property, a `config` subdirectory, or the current directory — the latter two resolved relative to the directory the app is launched from.

| File | 1 (highest) | 2 | 3 (lowest) |
| --- | --- | --- | --- |
| `application.properties` | `-Dspring.config.location=...` | `config` subdirectory | Same directory |
| `logback-spring.xml` | `-Dlogging.config=...` | `config` subdirectory | Same directory |

For example, with `application.properties` (`logback-spring.xml` works identically — just swap the filename):

```
/your-work-dir/
├── ecuacion-tool-housekeep-db-x.x.x.jar
├── application.properties   ← priority 3
└── config/
    └── application.properties   ← priority 2, higher
```

`java -jar` must be run from `/your-work-dir` for either of these to be found — "same directory" and "`config` subdirectory" are both resolved relative to the directory the app is launched from, not the directory the JAR file happens to live in.

To specify an explicit path instead:

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-housekeep-db-x.x.x.jar
```

> **Note:** `-Dspring.config.location` points at a **directory**, not a single file — pointing it at a single file loads only that file. `logback-spring.xml` uses its own system property (`-Dlogging.config`, shown in the table above) and isn't affected by `-Dspring.config.location`.
