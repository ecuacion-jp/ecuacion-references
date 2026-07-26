## application.properties

Spring Boot's external configuration files are loaded in the following priority order (higher overrides lower):

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dspring.config.location=...` |
| 2 | `config/application.properties`, in a `config` subdirectory next to the WAR |
| 3 (lowest) | `application.properties`, right next to the WAR |

> **Note:** External files don't replace the embedded configuration — they're **merged** into it. Only the keys explicitly defined in the external file are overridden; every other embedded setting stays in effect.

### Access Control

Two properties control access to `executeScript` (see [API Spec](page?id=command-api/api-spec&lang=en) for the resulting HTTP behavior). They are intentionally **not** set in the embedded `application.properties`, so leaving either unconfigured is logged as a warning at startup instead of silently defaulting.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.allow-insecure-access` | boolean | `true`: `GET` is allowed, and `POST` skips `apiKey` verification. Intended for trusted internal networks only. `false` (the default applied when unset): `GET` is rejected (403), and `POST` requires a valid `apiKey`. |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to a file containing the shared secret compared against the `apiKey` POST parameter. Supports `${ENV_VAR}` resolution, same as script paths (see below). |

Example:

```properties
jp.ecuacion.tool.command-api.allow-insecure-access=false
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

The api-key file's content is read (with surrounding whitespace/newlines trimmed) on every request, so the key can be rotated by replacing the file's content without restarting the app.

### Using a Custom application.properties

Place it next to the WAR, or in a `config` subdirectory.

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties   ← overrides the embedded configuration
└── config/
    └── application.properties   ← also works here (higher priority)
```

To specify an explicit path, use a system property.

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

> **Note:** Pointing at a single file loads only that file. If you also want `ecuacion-tool-command-api.properties` (described below) to be externalized, point at a **directory** instead.

---

## ecuacion-tool-command-api.properties

Used for script registration. It's loaded following the exact same priority and placement rules as `application.properties`.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dspring.config.location=...` |
| 2 | `config/ecuacion-tool-command-api.properties`, in a `config` subdirectory next to the WAR |
| 3 (lowest) | `ecuacion-tool-command-api.properties`, right next to the WAR |

### Using a Custom ecuacion-tool-command-api.properties

Place it next to the WAR, or in a `config` subdirectory.

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── ecuacion-tool-command-api.properties   ← script registration
└── config/
    └── ecuacion-tool-command-api.properties   ← also works here (higher priority)
```

To specify an explicit path, use a system property. Pointing at a single file loads only that file — point at a **directory** instead if you also want `application.properties` to be externalized at the same time.

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

### Available Settings

#### Registering Scripts

Register scripts in `ecuacion-tool-command-api.properties` using the following format.

```properties
script.<script-id>=<absolute path to script>
```

Example:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

#### Using Environment Variables

Environment variables can be used in script paths using the `${ENV_VAR}` syntax.

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

---

## logback-spring.xml

The Logback configuration file is loaded in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dlogging.config=...` |
| 2 (lowest) | `config/logback-spring.xml`, relative to the current directory |

> **Note:** "Current directory" for priority 2 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it (as shown below), this is effectively the same as "the `config/` directory next to the WAR." If you launch from elsewhere, the search is relative to that directory instead.

### Using a Custom logback-spring.xml

**Option 1 — place it in a `config/` subdirectory (recommended):**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-command-api-x.x.x.war
```

**Option 2 — specify the path explicitly:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-command-api-x.x.x.war
```

### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>./logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>./logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.springframework" level="WARN" />
    <logger name="jp.ecuacion" level="INFO" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```

Main things to adjust:

| Item | Where to change | Typical values |
| --- | --- | --- |
| Overall log level | `<root level="...">` | `DEBUG`, `INFO`, `WARN`, `ERROR` |
| Per-package level | `<logger name="..." level="...">` | Same as above |
| Log file path | `<file>` / `<fileNamePattern>` | Any writable path |
| Retention days | `<maxHistory>` | Number of days |

---

## Deploying to an Existing Tomcat

Since there's no "next to the WAR" location in this case, use a directory specified via the `CLASSPATH` environment variable instead. For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

`application.properties` / `ecuacion-tool-command-api.properties` placed in this directory are automatically merged in via Spring Boot's `classpath:` search. If you want to replace `logback-spring.xml`, you still need to specify the path with `-Dlogging.config` in this case too.
