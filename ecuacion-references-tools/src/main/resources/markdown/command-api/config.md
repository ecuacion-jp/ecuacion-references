## application.properties

Spring Boot's external configuration files are loaded in the following priority order (higher overrides lower):

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dspring.config.location=...` |
| 2 | `config/application.properties`, in a `config` subdirectory next to the WAR |
| 3 (lowest) | `application.properties`, right next to the WAR |

> **Note:** External files don't replace the embedded configuration — they're **merged** into it. Only the keys explicitly defined in the external file are overridden; every other embedded setting stays in effect.

> **Note:** Changes to this file can be picked up without restarting the app, via `ecuacion-splib-rest`'s `clearPropertiesCache` endpoint (`POST /api/ecuacion-splib/key/clearPropertiesCache`) — see [Operational Endpoints](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=en) for how it works and its limitations. That endpoint is authenticated with a separate, built-in API key, not the `api-key-file-path` key described below.

### Placement (Optional)

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

> **Note:** Pointing at a single file loads only that file. If you also want `ecuacion-tool-command-api.properties` (below) to be externalized, point at a **directory** instead.

### Available Settings

Additional settings should be written in `application.properties`.

#### Access Control

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | Whether `api/public/executeScript` is disabled, requiring `api/key/executeScript` with a valid `X-Api-Key` header instead. Default: `true`. |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to the file holding the shared secret compared against `X-Api-Key`. |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | Whether the api-key file's lines are compared as plain text (`PLAIN`) or bcrypt hashes (`BCRYPT`). Default: `PLAIN`. |

See [Access Control](page?id=command-api/access-control&lang=en) for the full explanation of each property, plus how the api-key file itself is managed.

#### Mail Notification (on Error)

Uses `SplibMailUtil` to notify administrators by mail when a system error occurs. For the full list
of `spring.mail.*` / `jp.ecuacion.splib.mail.*` properties, their defaults, and example
configurations (including Gmail), see
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=en).

---

## ecuacion-tool-command-api.properties

Used for script registration. It's loaded following the exact same priority and placement rules as `application.properties`.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dspring.config.location=...` |
| 2 | `config/ecuacion-tool-command-api.properties`, in a `config` subdirectory next to the WAR |
| 3 (lowest) | `ecuacion-tool-command-api.properties`, right next to the WAR |

> **Note:** Unlike `application.properties`, changes to this file are **not** picked up by the `clearPropertiesCache` endpoint — see [Operational Endpoints](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=en) for why (it only reliably reloads the *primary* `spring.config.name`, and this file is registered under an additional one). Restart the app to apply script registration changes.

### Placement

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

#### Restricting the Allowed HTTP Method

Prefix a script definition's value with `GET:`, `POST:`, or `ALL:` (case-insensitive) to control which HTTP method(s) can invoke that script on `api/public/executeScript` (once enabled) and `api/key/executeScript` alike. Omitting the prefix allows `POST` only.

```properties
script.say-hello=GET:/opt/scripts/sayHello.sh
script.daily-batch=POST:/opt/scripts/dailyBatch.sh
script.status-check=ALL:/opt/scripts/statusCheck.sh
script.legacy-job=/opt/scripts/legacyJob.sh
```

| Script ID | Allowed method(s) |
| --- | --- |
| `script.say-hello` | `GET` only |
| `script.daily-batch` | `POST` only |
| `script.status-check` | `GET` and `POST` |
| `script.legacy-job` | `POST` only (the default when the prefix is omitted) |

> **Note:** This prefix applies identically to `api/public/executeScript` and `api/key/executeScript`. The only difference between the two endpoints is whether `X-Api-Key` header authentication is required, not this method restriction.

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
| 2 | `config/logback-spring.xml`, relative to the current directory |
| 3 (lowest) | `logback-spring.xml`, directly in the current directory |

> **Note:** "Current directory" for priorities 2 and 3 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it (as shown below), this is effectively the same as "next to the WAR." If you launch from elsewhere, the search is relative to that directory instead.
>
> Priorities 2 and 3 aren't a Spring Boot feature — they're an ecuacion-specific extension provided by `ecuacion-splib-core` (`SplibEnvironmentPostProcessor`), added to match `application.properties`'s behavior by automatically checking both `config/` and the current directory root.

### Placement

Place it in a `config` subdirectory, or directly in the current directory the app is launched from (see the note above).

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── logback-spring.xml   ← also works here
└── config/
    └── logback-spring.xml   ← or here (higher priority)
```

To specify an explicit path, use a system property.

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

---

## Deploying to an Existing Tomcat

Since there's no "next to the WAR" location in this case, an external directory is instead surfaced through Spring Boot's `classpath:` search. There are two ways to do this.

### Option 1 — Point `CLASSPATH` via `setenv.sh`

For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

`application.properties` / `ecuacion-tool-command-api.properties` placed in this directory are automatically merged in via Spring Boot's `classpath:` search. If you want to replace `logback-spring.xml`, you still need to specify the path with `-Dlogging.config` in this case too.

> **Note:** `CLASSPATH` is shared by the entire Tomcat process. If you co-locate multiple ecuacion apps (e.g. `ecuacion-tool-command-api` and `ecuacion-tool-code-generator`) on the same Tomcat, they'd end up sharing the same config directory, which gets unwieldy. Use Option 2 if you want each app to have its own config.

### Option 2 — Point to an app-specific directory via `META-INF/context.xml` (recommended)

No need to edit `setenv.sh`, and multiple ecuacion apps can be co-located on the same Tomcat without their config files colliding. The `ecuacion-tool-command-api` WAR ships with the following `META-INF/context.xml` already bundled.

```xml
<Context>
	<Resources>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf/ecuacion-tool-command-api"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
		<PreResources className="org.apache.catalina.webresources.DirResourceSet"
				base="${catalina.base}/app-conf"
				webAppMount="/WEB-INF/classes"
				readOnly="true"/>
	</Resources>
</Context>
```

Both the app-specific directory (`app-conf/ecuacion-tool-command-api`) and the shared `app-conf` directory itself are mounted. If the same file exists in both, the app-specific one wins; a file present in only one of them is still picked up either way (a directory-level overlay).

- **If this Tomcat only hosts the `ecuacion-tool-command-api` WAR:** you can drop config files directly under `app-conf` — no need to create the deeper `app-conf/ecuacion-tool-command-api/` path.
- **If multiple ecuacion apps are co-located:** put any file you want scoped to this app specifically under `app-conf/ecuacion-tool-command-api/` (it takes priority over `app-conf`).

> **PREREQUISITE:** The following directory must exist on the server before deployment. If it does not exist, Tomcat will fail to start with an `IllegalArgumentException`.
>
> ```
> ${catalina.base}/app-conf/ecuacion-tool-command-api/
>   (e.g. /usr/local/tomcat/app-conf/ecuacion-tool-command-api/)
> ```
>
> Creating this directory with `mkdir -p` also creates its `${catalina.base}/app-conf/` parent as a side effect, so a single command satisfies both prerequisites.

Placing `application.properties` / `ecuacion-tool-command-api.properties` / `logback-spring.xml` in this directory gets them all picked up automatically (with this option, `logback-spring.xml` is picked up without needing `-Dlogging.config` either).
