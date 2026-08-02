## application.properties

Spring Boot external configuration files are loaded in the following priority order (higher entries override lower ones):

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dspring.config.location=...` |
| 2 | `./config/application.properties` (in a `config/` subdirectory of the working directory) |
| 3 (lowest) | `./application.properties` (in the working directory, next to the WAR) |

> **Note:** In the `application.properties` you create, you only need to write the settings you want to change. Any setting you don't write keeps the default value listed below.

### Using a custom application.properties

Place your file in the same directory as the WAR or in a `config/` subdirectory:

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
├── application.properties          ← overrides embedded settings
└── config/
    └── application.properties      ← alternatively, place it here (higher priority)
```

You can also specify the config file location explicitly:

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

### Available properties

Additional settings should be written in `application.properties`.

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

Logback configuration is resolved as follows:

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dlogging.config=...` |
| 2 | `config/logback-spring.xml`, relative to the current directory |
| 3 (lowest) | `logback-spring.xml`, directly in the current directory |

> **Note:** "Current directory" for priorities 2 and 3 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it (as shown below), this is effectively the same as "next to the WAR." If you launch from elsewhere, the search is relative to that directory instead.
>
> Priorities 2 and 3 aren't a Spring Boot feature — they're an ecuacion-specific extension provided by `ecuacion-splib-core` (`SplibEnvironmentPostProcessor`), added to match `application.properties`'s behavior by automatically checking both `config/` and the current directory root.

### Using a custom logback-spring.xml

**Option 1 — Place in `config/` subdirectory (recommended):**

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

**Option 1b — Place directly in the current directory:**

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

**Option 2 — Specify path explicitly:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

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

Key things to adjust:

| Item | Where to change | Common values |
| --- | --- | --- |
| Overall log level | `<root level="...">` | `DEBUG`, `INFO`, `WARN`, `ERROR` |
| Package-specific level | `<logger name="..." level="...">` | Same as above |
| Log file path | `<file>` and `<fileNamePattern>` | Any writable path |
| Retention period | `<maxHistory>` | Number of days |

---

## Deploying to an Existing Tomcat

Since there's no "next to the WAR" location in this case, an external directory is instead surfaced through Spring Boot's `classpath:` search. There are two ways to do this.

### Option 1 — Point `CLASSPATH` via `setenv.sh`

For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

`application.properties` placed in this directory is automatically merged in via Spring Boot's `classpath:` search. If you want to replace `logback-spring.xml`, you still need to specify the path with `-Dlogging.config` in this case too.

> **Note:** `CLASSPATH` is shared by the entire Tomcat process. If you co-locate multiple ecuacion apps (e.g. `ecuacion-tool-code-generator` and `ecuacion-tool-command-api`) on the same Tomcat, they'd end up sharing the same config directory, which gets unwieldy. Use Option 2 if you want each app to have its own config.

### Option 2 — Point to an app-specific directory via `META-INF/context.xml` (recommended)

No need to edit `setenv.sh`, and multiple ecuacion apps can be co-located on the same Tomcat without their config files colliding. The `ecuacion-tool-code-generator-web` WAR ships with the following `META-INF/context.xml` already bundled.

```xml
<Context>
	<Resources>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf/ecuacion-tool-code-generator"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
	</Resources>
</Context>
```

Both the app-specific directory (`app-conf/ecuacion-tool-code-generator`) and the shared `app-conf` directory itself are mounted. If the same file exists in both, the app-specific one wins; a file present in only one of them is still picked up either way (a directory-level overlay).

- **If this Tomcat only hosts the `ecuacion-tool-code-generator-web` WAR:** you can drop config files directly under `app-conf` — no need to create the deeper `app-conf/ecuacion-tool-code-generator/` path.
- **If multiple ecuacion apps are co-located:** put any file you want scoped to this app specifically under `app-conf/ecuacion-tool-code-generator/` (it takes priority over `app-conf`).

> **PREREQUISITE:** The following directory must exist on the server before deployment. If it does not exist, Tomcat will fail to start with an `IllegalArgumentException`.
>
> ```
> ${catalina.base}/app-conf/ecuacion-tool-code-generator/
>   (e.g. /usr/local/tomcat/app-conf/ecuacion-tool-code-generator/)
> ```
>
> Creating this directory with `mkdir -p` also creates its `${catalina.base}/app-conf/` parent as a side effect, so a single command satisfies both prerequisites.

Placing `application.properties` / `logback-spring.xml` in this directory gets them both picked up automatically (with this option, `logback-spring.xml` is picked up without needing `-Dlogging.config` either).
