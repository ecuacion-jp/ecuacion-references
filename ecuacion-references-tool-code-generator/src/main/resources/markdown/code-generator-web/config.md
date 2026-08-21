This page covers the two config files used by `ecuacion-tool-code-generator-web` — `application.properties` and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a code-generator-web-specific mechanism — it's a Spring Boot feature. For where to place this file, see [File Placement](#file-placement) below.

### Available properties

Additional settings should be written in `application.properties`. You only need to write the settings you want to change — any setting you don't write keeps the default value listed below.

#### Application settings

| Property | Description | Default |
| --- | --- | --- |
| `work-dir` | Base directory for temporary working files | `./app-work` |

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

Placement for `application.properties` is standard Spring Boot externalized-configuration behavior — nothing code-generator-web-specific. `logback-spring.xml` is *almost* the same, except Spring Boot doesn't provide the equivalent lookup on its own, so `ecuacion-splib-core` adds it. Both follow the same three-tier lookup: an explicit path via a system property, a `config` subdirectory, or a default fallback location — the latter two resolved relative to the current directory the app is launched from.

<table>
<thead>
<tr><th>File</th><th>1 (highest)</th><th>2</th><th>3 (lowest)</th></tr>
</thead>
<tbody>
<tr><td><code>application.properties</code></td><td><code>-Dspring.config.location=...</code></td><td rowspan="2"><code>config</code> subdirectory</td><td rowspan="2">Same directory</td></tr>
<tr><td><code>logback-spring.xml</code></td><td><code>-Dlogging.config=...</code></td></tr>
</tbody>
</table>

For example, with `application.properties` (`logback-spring.xml` works identically — just swap the filename):

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
├── application.properties   ← priority 3
└── config/
    └── application.properties   ← priority 2, higher
```

To specify an explicit path instead:

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-web-x.x.x.war

java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

> **Note:** "Current directory" for priorities 2 and 3 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it, this is effectively the same as "next to the WAR." If you launch from elsewhere, the search is relative to that directory instead.

### Deploying to an Existing Tomcat

Since there's no "next to the WAR" location in this case, an external directory is instead surfaced through Spring Boot's `classpath:` search. There are two ways to do this.

#### Option 1 — App-Specific Directory via `jp.ecuacion.tool.code-generator.app-conf-dir`

The directory set via the `jp.ecuacion.tool.code-generator.app-conf-dir` system property is added to the classpath. It's created automatically if it doesn't already exist, so there's no need to prepare it in advance. If left unset, nothing is mounted, and the WAR's own embedded configuration is used as-is.

An app-specific classpath directory keeps multiple apps co-located on the same Tomcat from colliding on config files.

The usual way to set it is as `CATALINA_OPTS` in `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

Placing `application.properties` / `logback-spring.xml` in this directory gets them both picked up automatically (with this option, `logback-spring.xml` is picked up without needing `-Dlogging.config` either).

#### Option 2 — Point `CLASSPATH` via `setenv.sh`

For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

`application.properties` placed in this directory is automatically merged in via Spring Boot's `classpath:` search. If you want to replace `logback-spring.xml`, you still need to specify the path with `-Dlogging.config` in this case too.

> **Note:** `CLASSPATH` is shared by the entire Tomcat process. If you co-locate multiple ecuacion apps (e.g. `ecuacion-tool-code-generator` and `ecuacion-tool-command-api`) on the same Tomcat, they'd end up sharing the same config directory, which gets unwieldy. Use Option 1 if you want each app to have its own config.
