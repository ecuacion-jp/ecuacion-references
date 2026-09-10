This page covers the two config files used by `ecuacion-tool-housekeep-files` — `application.properties` and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a housekeep-files-specific mechanism — it's a Spring Boot feature. `application.yml` / `application.yaml` work exactly the same way. See [Spring Boot's own reference](https://docs.spring.io/spring-boot/reference/features/external-config.html) for the full picture. For where to place this file, see [File Placement](#file-placement) below.

### Available Settings

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `jp.ecuacion.tool.housekeep-files.excel-path` | ○ | — | Path to the excel configuration file to run |
| `jp.ecuacion.tool.housekeep-files.system-name` | — | —<br>(omitted if unset) | System name shown in job start/finish logs and the warning email subject.<br>If unset, that part is simply omitted |
| `jp.ecuacion.tool.housekeep-files.sftp.strict-host-key-checking` | — | `true`<br>(checking enabled) | Set to `false` to disable SFTP host key verification.<br>This allows man-in-the-middle attacks to go undetected, so never use it other than for quick, throwaway local trials |

```properties
jp.ecuacion.tool.housekeep-files.excel-path=/path/to/your-settings.xlsx
```

### Using Path Variables

The `${VAR_NAME}` path variables used in the Task Settings sheet's "Source Path" and
"Dest Path" columns are defined in `application.properties` (or, since Spring Boot resolves
these regardless of source, OS environment variables, JVM system properties, or command-line
arguments work just as well).

```properties
BASE_DIR=/data/myapp
```

Reference it in the Task Settings sheet as `${BASE_DIR}`.

> **Note:** `YYYYMMDD` / `TIMESTAMP` / `HOSTNAME` are reserved as built-in
> variables — setting a property with the same name doesn't override the built-in value.

---

## logback-spring.xml

The Logback configuration file. You're of course free to configure it however you like, but
splib ships built-in include resources for appenders/loggers, so using them lets you keep it
this concise.

### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="logback-spring-app-common.xml" />

    <property name="log-dir" value="/path/to/logs/directory" />
    <include resource="logback-spring-appenders-batch.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-batch-to-console-and-files.xml" />
</configuration>
```

---

## File Placement

Placement for `application.properties` is standard Spring Boot externalized-configuration behavior — nothing housekeep-files-specific. `logback-spring.xml` is *almost* the same, except Spring Boot doesn't provide the equivalent lookup on its own, so `ecuacion-splib-core` adds it. Both follow the same three-tier lookup: an explicit path via a system property, a `config` subdirectory, or the current directory — the latter two resolved relative to the directory the app is launched from.

| File | 1 (highest) | 2 | 3 (lowest) |
| --- | --- | --- | --- |
| `application.properties` | `-Dspring.config.location=...` | `config` subdirectory | Same directory |
| `logback-spring.xml` | `-Dlogging.config=...` | `config` subdirectory | Same directory |

For example, with `application.properties` (`logback-spring.xml` works identically — just swap the filename):

```
/your-work-dir/
├── ecuacion-tool-housekeep-files-x.x.x.jar
├── application.properties   ← priority 3
└── config/
    └── application.properties   ← priority 2, higher
```

`java -jar` must be run from `/your-work-dir` for either of these to be found — "same directory" and "`config` subdirectory" are both resolved relative to the directory the app is launched from, not the directory the JAR file happens to live in.

To specify an explicit path instead:

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-housekeep-files-x.x.x.jar
```

> **Note:** `-Dspring.config.location` points at a **directory**, not a single file — pointing it at a single file loads only that file. `logback-spring.xml` uses its own system property (`-Dlogging.config`, shown in the table above) and isn't affected by `-Dspring.config.location`.
