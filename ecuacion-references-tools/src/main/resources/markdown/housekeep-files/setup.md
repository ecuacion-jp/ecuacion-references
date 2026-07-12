# housekeep-files Setup

## 1. Download the JAR File

Download the JAR file from the Maven repository:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/
```

Example URL (replace `x.x.x` with the actual version):

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/x.x.x/ecuacion-tool-housekeep-files-x.x.x.jar
```

## 2. Download the Sample Excel Configuration File

Download the sample file from GitHub to use as a template:

```
https://github.com/ecuacion-jp/ecuacion-tools/tree/main/ecuacion-tool-housekeep-files/sample
```

File name: `housekeep-files(fmt-v1.3.0)_sample-1.xlsx`

## 3. Log Configuration (Optional)

To customize log output, create `logback-spring.xml` and place it in a CLASSPATH directory.

Specifying the CLASSPATH directory:

```
java -jar ecuacion-tool-housekeep-files-x.x.x.jar --classpath=/path/to/classpath/directory excelPath=/path/to/settings.xlsx
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
- Linux or macOS (Windows is not supported)
