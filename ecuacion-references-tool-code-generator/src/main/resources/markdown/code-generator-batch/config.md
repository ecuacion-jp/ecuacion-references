## application.properties

Spring Boot external configuration files are loaded in the following priority order (higher entries override lower ones):

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dspring.config.location=...` |
| 2 | `./config/application.properties` (in a `config/` subdirectory of the working directory) |
| 3 (lowest) | `./application.properties` (in the working directory, next to the JAR) |

> **Note:** In the `application.properties` you create, you only need to write the settings you want to change. Any setting you don't write keeps the default value listed below.

### Using a custom application.properties

Place your file in the same directory as the JAR or in a `config/` subdirectory:

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
├── application.properties          ← overrides embedded settings
└── config/
    └── application.properties      ← alternatively, place it here (higher priority)
```

You can also specify the config file location explicitly:

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

### Available properties

Additional settings should be written in `application.properties`.

#### Input / Output Directories

| Property | Description | Default |
| --- | --- | --- |
| `input-dir` | Directory containing the Excel specification files. Multiple directories can be specified as a comma-separated list (e.g. `./dir1,./dir2`) | `./excel-format` |
| `output-dir` | Root directory for generated Java source files | `./products/` |

#### Mail notification (on batch failure)

Uses `SplibMailUtil` to notify administrators by mail when a batch job fails. For the full list of
`spring.mail.*` / `jp.ecuacion.splib.mail.*` properties, their defaults, and example configurations
(including Gmail), see
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=en).

---

## logback-spring.xml

Logback configuration is resolved as follows:

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dlogging.config=...` |
| 2 (lowest) | `config/logback-spring.xml`, relative to the current directory |

> **Note:** "Current directory" for priority 2 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the JAR before starting it (as shown below), this is effectively the same as "the `config/` directory next to the JAR." If you launch from elsewhere, the search is relative to that directory instead. Also, unlike `application.properties`, a `logback-spring.xml` placed directly next to the JAR (without a `config/` subdirectory) is **not** automatically picked up.

### Using a custom logback-spring.xml

**Option 1 — Place in `config/` subdirectory (recommended):**

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

**Option 2 — Specify path explicitly:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-batch-x.x.x.jar
```

### Example configuration

A minimal example that logs to both the console and a file:

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

Key things to adjust:

| Item | Where to change | Common values |
| --- | --- | --- |
| Overall log level | `<root level="...">` | `DEBUG`, `INFO`, `WARN`, `ERROR` |
| Package-specific level | `<logger name="..." level="...">` | Same as above |
| Log file path | `<file>` and `<fileNamePattern>` | Any writable path |
| Retention period | `<maxHistory>` | Number of days |
