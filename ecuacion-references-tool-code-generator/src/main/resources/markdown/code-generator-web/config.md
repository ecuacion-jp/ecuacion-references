This page covers the two config files used by `ecuacion-tool-code-generator-web` — `application.properties` and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a code-generator-web-specific mechanism — it's a Spring Boot feature. For where to place this file, see [File Placement](#file-placement) below.

### Available properties

Additional settings should be written in `application.properties`. You only need to write the settings you want to change — any setting you don't write keeps the default value listed below.

#### Application settings

| Property | Description | Default |
| --- | --- | --- |
| `work-dir` | Base directory for temporary working files | `./app-work` |

> **Note:** The code generation endpoint (`/public/sourceDownload/action`) requires no authentication and can be invoked by anyone. The application itself has no request rate limiting or concurrency limiting, so if you expose it to the internet, configure rate limiting at your reverse proxy / WAF.

#### Mail notification (on error)

Uses `SplibMailUtil` to notify administrators by mail when a system error occurs. For the full list
of `spring.mail.*` / `jp.ecuacion.splib.mail.*` properties, their defaults, and example
configurations (including Gmail), see
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=en).

---

## logback-spring.xml

The Logback configuration file; placed as described in [File Placement](#file-placement) below.

### Example configuration

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

---

## File Placement

Where these two files go depends on how you're running `ecuacion-tool-code-generator-web`.

### Standalone

#### Without any options

Place both files either directly in the current directory the app is launched from, or in a `config` subdirectory of it. No options are needed.

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── config/
    ├── application.properties
    └── logback-spring.xml
```

> **Note:** "Current directory" means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it, this is effectively the same as "next to the WAR."

If files exist in both locations, `application.properties` is merged key by key (the `config` subdirectory wins on conflicts), while for `logback-spring.xml` only the one in the `config` subdirectory is used.

#### Placing the files in a directory of your choosing

Set the `jp.ecuacion.tool.code-generator.app-conf-dir` system property — the same one used when [deploying to an existing Tomcat](#deploying-to-an-existing-tomcat). Both files placed in that directory are picked up.

```bash
java -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

Use only one of the locations above at a time — mixing them makes it hard to tell which file's values end up in effect.

> **Note:** Spring Boot's own `-Dspring.config.location` also works, but it's not the recommended way. It replaces Spring Boot's default lookup locations entirely, so `jp.ecuacion.tool.code-generator.app-conf-dir` stops taking effect, and `logback-spring.xml` isn't covered by it — you'd have to point `-Dlogging.config` at the file separately.

### Deploying to an Existing Tomcat

`application.properties` and `logback-spring.xml` placed at `${catalina.base}/app-conf/ecuacion-tool-code-generator/` are picked up automatically — the WAR itself declares this location via Spring Boot's `spring.config.import` for the former, and `ecuacion-splib-core`'s `SplibEnvironmentPostProcessor` checks the same directory for the latter. No Tomcat-side configuration (`setenv.sh`, `context.xml`, etc.) is needed at all.

```
${CATALINA_HOME}/
└── app-conf/
    └── ecuacion-tool-code-generator/
        ├── application.properties
        └── logback-spring.xml
```

This directory doesn't need to exist beforehand — if it's missing, nothing is imported and the WAR's own embedded configuration is used as-is. Because the app name is baked into the default path, co-locating multiple ecuacion apps on the same Tomcat doesn't cause them to collide on config files by default.

The directory can still be redirected per deployment, without repackaging the WAR, via the `jp.ecuacion.tool.code-generator.app-conf-dir` system property — the usual way to set it is as `CATALINA_OPTS` in `${CATALINA_HOME}/bin/setenv.sh`. This redirects both files at once.

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

> **Note:** If `logging.config` is already set some other way (e.g. `-Dlogging.config` in `setenv.sh`, which applies to the entire Tomcat process), that takes precedence and the `logback-spring.xml` in this directory is ignored.
