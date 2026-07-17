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

To customize log output, create `logback-spring.xml` and place it in a CLASSPATH directory.

Specifying the CLASSPATH directory:

```
java -jar ecuacion-tool-housekeep-db-x.x.x.jar --classpath=/path/to/classpath/directory excelPath=/path/to/settings.xlsx
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
