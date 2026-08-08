## application.properties

`ecuacion-tool-housekeep-db` is a standard Spring Boot executable jar, so `application.properties` is loaded through Spring Boot's default external configuration mechanism as-is. It is loaded in the following priority order, letting you override or add to the settings bundled in the jar (currently only `spring.main.banner-mode=off`).

| Priority | Location |
| --- | --- |
| 1 (highest) | `config/application.properties`, relative to the current directory |
| 2 | `application.properties`, relative to the current directory |
| 3 (lowest) | `application.properties` bundled in the jar (default values) |

As with logback-spring.xml, placing it in a `config/` subdirectory is recommended.

```
/your-work-dir/
├── ecuacion-tool-housekeep-db-x.x.x.jar
└── config/
    ├── application.properties
    └── logback-spring.xml
```

This external placement overrides not only Spring-native properties such as `spring.*`, but also ecuacion-lib-specific properties such as `jp.ecuacion.locale.use-root`.

## logback-spring.xml

The Logback configuration file is loaded in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dlogging.config=...` |
| 2 (lowest) | `config/logback-spring.xml`, relative to the current directory |

> **Note:** "Current directory" for priority 2 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the JAR before starting it (as shown below), this is effectively the same as "the `config/` directory next to the JAR."

### Using a Custom logback-spring.xml

**Option 1 — Place in `config/` subdirectory (recommended):**

```
/your-work-dir/
├── ecuacion-tool-housekeep-db-x.x.x.jar
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-housekeep-db-x.x.x.jar excelPath=/path/to/settings.xlsx
```

**Option 2 — Specify path explicitly:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-housekeep-db-x.x.x.jar excelPath=/path/to/settings.xlsx
```

### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="log-dir" value="/path/to/logs/directory" />
    <property name="loglevel-spring" value="INFO" />
    <include resource="logback-spring-appenders.xml" />
    <include resource="logback-spring-appenders-local.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-security" value="INFO" />
    <property name="loglevel-sql" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-for-local.xml" />
</configuration>
```
