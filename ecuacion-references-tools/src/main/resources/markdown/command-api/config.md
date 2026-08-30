This page covers the three config files used by `ecuacion-tool-command-api` — `application.properties`, `ecuacion-tool-command-api-scripts.properties`, and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a command-api-specific mechanism — it's a Spring Boot feature. `application.yml` / `application.yaml` work exactly the same way. See [Spring Boot's own reference](https://docs.spring.io/spring-boot/reference/features/external-config.html) for the full picture. For where to place this file, see [File Placement](#file-placement) below.

### Available Settings

Additional settings should be written in `application.properties`.

#### Access Control

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | Whether `api/public/execute` is disabled, requiring `api/key/execute` with a valid `X-Api-Key` header instead. Default: `true`. |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | Path to the file holding the shared secret compared against `X-Api-Key`. |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | Whether the api-key file's lines are compared as plain text (`PLAIN`) or bcrypt hashes (`BCRYPT`). Default: `BCRYPT`. |

See [Access Control](page?id=command-api/access-control&lang=en) for the full explanation of each property, plus how the api-key file itself is managed.

#### Script Execution

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.script-timeout-seconds` | long | Maximum number of seconds to wait for a script to finish. On timeout, the script is forcibly killed and a `504 Gateway Timeout` is returned. Default: `60`. |
| `jp.ecuacion.tool.command-api.script-max-output-bytes` | long | Maximum number of bytes of stdout / stderr included in the response (applied independently to each stream). Lines received after the cap is reached are discarded, and the response's `stdoutTruncated` / `stderrTruncated` is set to `true`; the script itself still runs to completion. Default: `1048576` (1 MiB). |

Prevents a hanging script (or a script hung by a crafted parameter) from occupying a worker thread indefinitely, and prevents a script producing a large amount of output (e.g. `cat`-ing a large file, or an infinite-output loop) from exhausting the JVM heap.

#### Mail Notification (on Error)

Uses `SplibMailUtil` to notify administrators by mail when a system error occurs. For the full list
of `spring.mail.*` / `jp.ecuacion.splib.mail.*` properties, their defaults, and example
configurations (including Gmail), see
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=en).

---

## ecuacion-tool-command-api-scripts.properties

Used for script registration; placed the same way as `application.properties` (see [File Placement](#file-placement) below).

### Available Settings

#### Registering Scripts

Register scripts in `ecuacion-tool-command-api-scripts.properties` using the following format.

```properties
<script-id>=<absolute path to script>
```

Example:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

#### Restricting the Allowed HTTP Method

Prefix a script definition's value with `GET:`, `POST:`, or `ALL:` (case-insensitive) to control which HTTP method(s) can invoke that script on `api/public/execute` (once enabled) and `api/key/execute` alike. Omitting the prefix allows `POST` only.

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

> **Note:** This prefix applies identically to `api/public/execute` and `api/key/execute`. The only difference between the two endpoints is whether `X-Api-Key` header authentication is required, not this method restriction.

#### Using Environment Variables

Environment variables can be used in script paths using the `${ENV_VAR}` syntax.

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

---

## logback-spring.xml

The Logback configuration file; placed as described in [File Placement](#file-placement) below.

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

## File Placement

Where these three files go depends on how you're running `ecuacion-tool-command-api` — see [Launch Patterns](page?id=command-api/launch-patterns&lang=en) for the two ways to run it.

> **Note:** Changes to `application.properties` can be picked up without restarting the app, via `ecuacion-splib-rest`'s `clearPropertiesCache` endpoint (see [Operational Endpoints](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=en) for details). Changes to `ecuacion-tool-command-api-scripts.properties`, however, are **not** picked up this way (it only reliably reloads the *primary* `spring.config.name`, and this file is registered under an additional one) — restart the app to apply script registration changes.

### Standalone

Placement for the two `.properties` files is standard Spring Boot externalized-configuration behavior — nothing command-api-specific. `logback-spring.xml` is *almost* the same, except Spring Boot doesn't provide the equivalent lookup on its own, so `ecuacion-splib-core` adds it. All three follow the same three-tier lookup: an explicit path via a system property, a `config` subdirectory, or a default fallback location — the latter two resolved relative to the current directory the app is launched from.

<table>
<thead>
<tr><th>File</th><th>1 (highest)</th><th>2</th><th>3 (lowest)</th></tr>
</thead>
<tbody>
<tr><td><code>application.properties</code></td><td rowspan="2"><code>-Dspring.config.location=...</code></td><td rowspan="3"><code>config</code> subdirectory</td><td rowspan="3">Same directory</td></tr>
<tr><td><code>ecuacion-tool-command-api-scripts.properties</code></td></tr>
<tr><td><code>logback-spring.xml</code></td><td><code>-Dlogging.config=...</code></td></tr>
</tbody>
</table>

For example, with `application.properties` (the other two work identically — just swap the filename):

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── application.properties   ← priority 3
└── config/
    └── application.properties   ← priority 2, higher
```

To specify an explicit path instead:

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-command-api-x.x.x.war
```

> **Note:** `-Dspring.config.location` covers both `.properties` files. Pointing it at a single file loads only that file — point at a **directory** if you want `application.properties` and `ecuacion-tool-command-api-scripts.properties` externalized together. `logback-spring.xml` uses its own system property (`-Dlogging.config`, shown in the table above) and isn't affected by `-Dspring.config.location`.

### Deploying to an Existing Tomcat

Since there's no "next to the WAR" location in this case, an external directory is instead surfaced through Spring Boot's `classpath:` search. There are two ways to do this.

#### Option 1 — App-Specific Directory via `jp.ecuacion.tool.command-api.app-conf-dir`

An app-specific classpath directory keeps multiple apps co-located on the same Tomcat from colliding on config files. Set the `jp.ecuacion.tool.command-api.app-conf-dir` system property to point it at a directory of your choosing. Left unset, `${catalina.base}/app-conf/ecuacion-tool-command-api` is used by default.

The usual way to set it is as `CATALINA_OPTS` in `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

#### Option 2 — Point `CLASSPATH` via `setenv.sh`

For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`.

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

`application.properties` / `ecuacion-tool-command-api-scripts.properties` placed in this directory are automatically merged in via Spring Boot's `classpath:` search. If you want to replace `logback-spring.xml`, you still need to specify the path with `-Dlogging.config` in this case too.

> **Note:** `CLASSPATH` is shared by the entire Tomcat process. If you co-locate multiple ecuacion apps (e.g. `ecuacion-tool-command-api` and `ecuacion-tool-code-generator`) on the same Tomcat, they'd end up sharing the same config directory, which gets unwieldy. Use Option 1 if you want each app to have its own config.
