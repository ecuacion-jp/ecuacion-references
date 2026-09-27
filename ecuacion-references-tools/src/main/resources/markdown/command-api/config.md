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

#### Using Variable References

Script paths can use `${VAR_NAME}` variable references. Values are resolved from
`application.properties`, OS environment variables, JVM system properties, or any other source
Spring Boot's `Environment` can resolve.

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

#### Without any options

Place all three files either directly in the current directory the app is launched from, or in a `config` subdirectory of it. No options are needed.

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    ├── application.properties
    ├── ecuacion-tool-command-api-scripts.properties
    └── logback-spring.xml
```

> **Note:** "Current directory" means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the WAR before starting it, this is effectively the same as "next to the WAR."

If files exist in both locations, the two `.properties` files are merged key by key (the `config` subdirectory wins on conflicts), while for `logback-spring.xml` only the one in the `config` subdirectory is used.

#### Placing the files in a directory of your choosing

Set the `jp.ecuacion.tool.command-api.app-conf-dir` system property — the same one used when [deploying to an existing Tomcat](#deploying-to-an-existing-tomcat). All three files placed in that directory are picked up.

```bash
java -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir \
     -jar ecuacion-tool-command-api-x.x.x.war
```

Use only one of the locations above at a time — mixing them makes it hard to tell which file's values end up in effect.

> **Note:** Spring Boot's own `-Dspring.config.location` also works, but it's not the recommended way. It replaces Spring Boot's default lookup locations entirely, so `jp.ecuacion.tool.command-api.app-conf-dir` stops taking effect, and `logback-spring.xml` isn't covered by it — you'd have to point `-Dlogging.config` at the file separately. If you do use it, point it at a **directory** (ending in `/`); pointing it at a single file loads only that file, and `ecuacion-tool-command-api-scripts.properties` won't be picked up.

### Deploying to an Existing Tomcat

`application.properties`, `ecuacion-tool-command-api-scripts.properties`, and `logback-spring.xml` placed at `${catalina.base}/app-conf/ecuacion-tool-command-api/` are all picked up automatically — the WAR itself declares this location via Spring Boot's `spring.config.import` for the two `.properties` files, and `ecuacion-splib-core`'s `SplibEnvironmentPostProcessor` checks the same directory for `logback-spring.xml`. No Tomcat-side configuration (`setenv.sh`, `context.xml`, etc.) is needed at all.

```
${CATALINA_HOME}/
└── app-conf/
    └── ecuacion-tool-command-api/
        ├── application.properties
        ├── ecuacion-tool-command-api-scripts.properties
        └── logback-spring.xml
```

This directory doesn't need to exist beforehand — if it's missing, nothing is imported and the WAR's own embedded configuration is used as-is. Because the app name is baked into the default path, co-locating multiple ecuacion apps on the same Tomcat doesn't cause them to collide on config files by default.

The directory can still be redirected per deployment, without repackaging the WAR, via the `jp.ecuacion.tool.command-api.app-conf-dir` system property — the usual way to set it is as `CATALINA_OPTS` in `${CATALINA_HOME}/bin/setenv.sh`. This redirects all three files at once.

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

> **Note:** If `logging.config` is already set some other way (e.g. `-Dlogging.config` in `setenv.sh`, which applies to the entire Tomcat process), that takes precedence and the `logback-spring.xml` in this directory is ignored.
