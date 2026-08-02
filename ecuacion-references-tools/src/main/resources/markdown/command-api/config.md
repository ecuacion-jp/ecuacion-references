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
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true` (the default applied when unset): `api/public/executeScript` is rejected (403). Call `api/key/executeScript` instead, which always requires a valid `X-Api-Key` header. `false`: `api/public/executeScript` is enabled. Use this only on trusted internal networks. (On both endpoints, which HTTP method(s) a script accepts is controlled by its HTTP-method prefix, described below.) |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to a file containing the shared secret compared against the `X-Api-Key` header on `api/key/executeScript`. Supports `${ENV_VAR}` resolution, same as script paths (see below). Optional — see [ecuacion-tool-command-api-key.txt](#ecuacion-tool-command-api-key.txt) below for the default applied when unset. |

Example:

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

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

## ecuacion-tool-command-api-key.txt

The file containing the shared secret compared against the `X-Api-Key` header on `api/key/executeScript` (see [Access Control](#access-control) above) is resolved in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `jp.ecuacion.tool.command-api.api-key-file-path` |
| 2 | `config/ecuacion-tool-command-api-key.txt`, relative to the current directory |
| 3 (lowest) | `ecuacion-tool-command-api-key.txt`, directly in the current directory |

Priorities 2 and 3 are a convenient zero-config default for casual/local use, with no `api-key-file-path` setting required at all.

> **Note:** Unlike `application.properties` / `ecuacion-tool-command-api.properties`, priorities 2 and 3 are a plain filesystem check relative to the JVM's working directory (`user.dir`) — they do **not** go through Spring Boot's `spring.config.location` / `classpath:` search. They work as described for the standalone `java -jar` launch style (see [Getting Started](page?id=command-api/quickstart&lang=en)). Under the [Tomcat deployment options](#deploying-to-an-existing-tomcat) below, `user.dir` is Tomcat's own working directory, unrelated to the WAR or `app-conf` overlay directories, so this default won't reliably find a file there — set `api-key-file-path` explicitly in that case.

The file's content is read (with surrounding whitespace/newlines trimmed) on every request, so the key can be rotated by replacing the file's content without restarting the app.

### Using a Custom ecuacion-tool-command-api-key.txt

**Option 1 — place it in a `config/` subdirectory:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── ecuacion-tool-command-api-key.txt
```

**Option 2 — place it directly next to the WAR:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── ecuacion-tool-command-api-key.txt
```

**Option 3 — specify the path explicitly (recommended for production):**

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

For production, prefer this option with a path outside the deployment directory (e.g. a secrets volume, or a location with tighter file permissions), so the key isn't bundled, backed up, or overwritten alongside the app.

### Comparison Mode: Plain Text vs. bcrypt

By default, every line in the file is compared as plain text. To store bcrypt hashes instead (so the raw keys aren't kept at rest anywhere the application can read them back), set the following property.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `PLAIN` (default): every line is a plain-text key, compared directly. `BCRYPT`: every line is a bcrypt hash, compared via `BCryptPasswordEncoder.matches`. An unrecognized value is logged as a warning at startup and treated as `PLAIN`. |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=BCRYPT
```

> **Note:** This mode applies to the **whole file** — every line must be the same kind (all plain, or all bcrypt hashes). Mixing plain-text keys and bcrypt hashes in the same file is not supported.

To generate a bcrypt hash for a key, for example:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

### Example

The file contains nothing but the shared secret(s) — no key name, no `properties` syntax. One key per line; if the file has more than one line, a request is accepted as long as it presents any one of them. Issuing one key per caller lets you revoke a single caller by deleting its line, without affecting the others.

Blank lines are skipped, and lines starting with `#` (after trimming leading whitespace) are treated as comments and skipped too — labeling which key belongs to which caller makes it easier to find the right line when a key needs revoking.

```
# key for client A
04f1befd704277c4b76afd01d655e6f1e8e36af9f74abe3a010d539ed3ac88cf

# key for client B
dcef325238aed9023681c8971d6df53080c536d0643692f9cad5a465118d5e79
```

A long, random value is recommended for each key. For `PLAIN` (the default), append the value as-is; for `BCRYPT`, hash it first before appending (see the hashing command example above). What callers must send in `X-Api-Key` is the pre-hash value, not the hash stored in the file.

Either way, a request is authenticated as long as its `X-Api-Key` header matches any one of the file's lines (after trimming surrounding whitespace/newlines).

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

**Option 1b — place it directly in the current directory:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
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
