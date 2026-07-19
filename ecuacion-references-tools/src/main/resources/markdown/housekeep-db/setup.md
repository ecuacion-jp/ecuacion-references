## 1. Download the JAR File

Download the latest `ecuacion-tool-housekeep-db-x.x.x.jar` from
[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tools/releases).

Place the JAR in any directory you prefer.

## 2. Download the Sample Excel Configuration File

Download the sample file from GitHub to use as a template:

```
https://github.com/ecuacion-jp/ecuacion-tools/tree/main/ecuacion-tool-housekeep-db/local-test
```

File: `housekeep-db(fmt-v1.3.0-en)_sample.xlsx` (English version)

## 3. Log Configuration (Optional)

The Logback configuration file is loaded in the following priority order.

| Priority | Location |
| --- | --- |
| 1 (highest) | The path given by `-Dlogging.config=...` |
| 2 (lowest) | `config/logback-spring.xml`, relative to the current directory |

> **Note:** "Current directory" for priority 2 means the working directory (`user.dir`) `java -jar` was run from. If you `cd` into the same directory as the JAR before starting it (as shown below), this is effectively the same as "the `config/` directory next to the JAR."

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

Example `logback-spring.xml`:

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

## System Requirements

- JDK 21 or above
- PostgreSQL (other databases are not supported at this time)
