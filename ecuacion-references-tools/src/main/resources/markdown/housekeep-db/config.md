This page covers the two config files used by `ecuacion-tool-housekeep-db` — `application.properties` and `logback-spring.xml` — and where to place them.

## application.properties

Reading this file isn't a housekeep-db-specific mechanism — it's a Spring Boot feature. `application.yml` / `application.yaml` work exactly the same way. See [Spring Boot's own reference](https://docs.spring.io/spring-boot/reference/features/external-config.html) for the full picture. For where to place this file, see [File Placement](#file-placement) below.

### Available Settings

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `jp.ecuacion.tool.housekeep-db.excel-path` | ○ | — | Path to the excel configuration file to run |
| `jp.ecuacion.tool.housekeep-db.max-select-lines` | — | `1000` | The number of rows the main SELECT retrieves and commits per loop iteration |
| `jp.ecuacion.tool.housekeep-db.target-system-name` | — | —<br>(omitted if unset) | System name shown in the job start log and the abnormal data check warning email subject.<br>If unset, that part is simply omitted |

```properties
jp.ecuacion.tool.housekeep-db.excel-path=/path/to/your-settings.xlsx
jp.ecuacion.tool.housekeep-db.max-select-lines=1000
jp.ecuacion.tool.housekeep-db.target-system-name=my-system
```

### Mail Settings

Used to send the error email when an error occurs, and the warning email when an [abnormal data check](page?id=housekeep-db/excel-settings/housekeep-db-settings&lang=en) finds records. The warning email goes to the same recipients as the error email.

| Property | Required | Default | Description |
| --- | --- | --- | --- |
| `spring.mail.host` | △ | — | SMTP server host name |
| `spring.mail.port` | — | `587` | SMTP server port |
| `spring.mail.username` | △ | — | SMTP authentication username (also used as the sender address) |
| `spring.mail.password` | △ | — | SMTP authentication password |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | △ | — | Recipients of the error / warning emails (comma-separated for multiple) |
| `jp.ecuacion.splib.mail.title-prefix` | — | (none) | String prepended to the email subject |

△: Required to send emails. If unset, no email is sent; that fact is logged at `WARN` level and processing continues.

The warning email subject has the form `<title-prefix>[WARN] HousekeepDb:<target-system-name>`.

> **Note:** Spring Boot reads `application.properties` as ISO-8859-1, so if `title-prefix` contains non-ASCII characters (e.g. Japanese), write it in `application.yml` (read as UTF-8) instead.

---

## logback-spring.xml

The Logback configuration file. You're of course free to configure it however you like, but
splib ships built-in include resources for appenders/loggers, so using them lets you keep it
this concise.

### Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="logback-spring-app-common.xml" />

    <property name="log-dir" value="/path/to/logs/directory" />
    <include resource="logback-spring-appenders-batch.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-batch-to-console-and-files.xml" />
</configuration>
```

---

## File Placement

Placement for `application.properties` is standard Spring Boot externalized-configuration behavior — nothing housekeep-db-specific. `logback-spring.xml` is *almost* the same, except Spring Boot doesn't provide the equivalent lookup on its own, so `ecuacion-splib-core` adds it. Both follow the same three-tier lookup: an explicit path via a system property, a `config` subdirectory, or the current directory — the latter two resolved relative to the directory the app is launched from.

| File | 1 (highest) | 2 | 3 (lowest) |
| --- | --- | --- | --- |
| `application.properties` | `-Dspring.config.location=...` | `config` subdirectory | Same directory |
| `logback-spring.xml` | `-Dlogging.config=...` | `config` subdirectory | Same directory |

For example, with `application.properties` (`logback-spring.xml` works identically — just swap the filename):

```
/your-work-dir/
├── ecuacion-tool-housekeep-db-x.x.x.jar
├── application.properties   ← priority 3
└── config/
    └── application.properties   ← priority 2, higher
```

`java -jar` must be run from `/your-work-dir` for either of these to be found — "same directory" and "`config` subdirectory" are both resolved relative to the directory the app is launched from, not the directory the JAR file happens to live in.

To specify an explicit path instead:

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-housekeep-db-x.x.x.jar
```

> **Note:** `-Dspring.config.location` points at a **directory**, not a single file — pointing it at a single file loads only that file. `logback-spring.xml` uses its own system property (`-Dlogging.config`, shown in the table above) and isn't affected by `-Dspring.config.location`.
