# command-api Configuration Files

## application.properties

Spring Boot's external configuration files are loaded in the following priority order (higher overrides lower):

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dspring.config.location=...` |
| 2 | `config/application.properties`, in a `config` subdirectory next to the WAR |
| 3 (lowest) | `application.properties`, right next to the WAR |

> **Note:** External files don't replace the embedded configuration — they're **merged** into it. Only the keys explicitly defined in the external file are overridden; every other embedded setting stays in effect.

`ecuacion-tool-command-api.properties` (used for script registration, described below) is loaded following the exact same priority and placement rules as `application.properties`.

### Using a Custom application.properties / ecuacion-tool-command-api.properties

Place it next to the WAR, or in a `config` subdirectory.

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties               ← overrides the embedded configuration
├── ecuacion-tool-command-api.properties  ← script registration
└── config/
    ├── application.properties            ← also works here (higher priority)
    └── ecuacion-tool-command-api.properties
```

To specify an explicit path, use a system property. If you also want `ecuacion-tool-command-api.properties` to be externalized, point at a **directory** rather than a single file (pointing at a single file loads only that file).

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

By default, INFO-level and above logs are written to the console. To change the destination or log level, specify a custom file's path with `-Dlogging.config`.

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
