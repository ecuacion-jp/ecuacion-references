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
| `jp.ecuacion.tool.codegenerator.input-dir` | Directory containing the Excel specification files. Multiple directories can be specified as a comma-separated list (e.g. `./dir1,./dir2`) | `./excel-format` |
| `jp.ecuacion.tool.codegenerator.output-dir` | Root directory for generated Java source files | `./products/` |

#### Mail notification (on batch failure)

SMTP connection settings use Spring Boot standard `spring.mail.*` properties.

> **Note:** The properties below have no default values. If any of `spring.mail.host` / `spring.mail.username` / `spring.mail.password` / `jp.ecuacion.splib.mail.address-csv-on-system-error` is left unset, no mail notification is sent on batch failure (i.e., the default behavior is "no mail is sent").

**SMTP settings (`spring.mail.*`):**

| Property | Description |
| --- | --- |
| `spring.mail.host` | SMTP server hostname |
| `spring.mail.port` | SMTP port (`587` for STARTTLS, `465` for SSL) |
| `spring.mail.username` | From address (used as SMTP login username) |
| `spring.mail.password` | SMTP password (or App Password for Gmail) |
| `spring.mail.properties.mail.smtp.auth` | `true` if SMTP authentication is required |
| `spring.mail.properties.mail.smtp.ssl.enable` | `true` for port 465 (SSL), `false` for port 587 (STARTTLS) |

**Application settings (`jp.ecuacion.splib.mail.*`):**

| Property | Description |
| --- | --- |
| `jp.ecuacion.splib.mail.title-prefix` | Prefix prepended to the email subject (e.g., environment label) |
| `jp.ecuacion.splib.mail.address-csv-on-system-error` | Comma-separated list of addresses to notify on system error |
| `jp.ecuacion.splib.mail.smtp.bounce-address` | Address that receives bounce/delivery-failure notifications (optional) |
| `jp.ecuacion.splib.mail.smtp.checks-certificate` | `false` to skip TLS certificate verification (optional, default `true`) |
| `jp.ecuacion.splib.mail.debug` | `true` to enable JavaMail debug output (optional, default `false`) |

**Example — general SMTP server (port 587 / STARTTLS):**

```properties
spring.mail.host=mail.example.com
spring.mail.port=587
spring.mail.username=no-reply@example.com
spring.mail.password=your-smtp-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[code-generator]
jp.ecuacion.splib.mail.address-csv-on-system-error=admin@example.com
```

**Example — Gmail (App Password required):**

> To use Gmail SMTP, enable 2-Step Verification on your Google account and generate an [App Password](https://myaccount.google.com/apppasswords). Use the App Password in place of your regular account password.

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-account@gmail.com
spring.mail.password=xxxx-xxxx-xxxx-xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=false

jp.ecuacion.splib.mail.title-prefix=[code-generator]
jp.ecuacion.splib.mail.address-csv-on-system-error=your-account@gmail.com
```

---

## logback-spring.xml

Logback configuration is resolved as follows:

| Priority | Location |
| --- | --- |
| 1 (highest) | Path specified by `-Dlogging.config=...` |
| 2 (lowest) | `./config/logback-spring.xml` (next to the JAR) |

> **Note:** Unlike `application.properties`, `logback-spring.xml` placed next to the JAR (without `config/`) is **not** automatically picked up by Spring Boot. Use the `config/` subdirectory or specify the path explicitly.

### Using a custom logback-spring.xml

**Option 1 — Place in `config/` subdirectory (recommended):**

```
/your-work-dir/
├── ecuacion-tool-code-generator-batch-x.x.x.jar
└── config/
    └── logback-spring.xml
```

```bash
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
