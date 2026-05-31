# command-api Setup

## 1. Download the WAR File

Download the WAR file from the Maven repository:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/
```

Example URL (replace `x.x.x` with the actual version):

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/x.x.x/ecuacion-tool-command-api-x.x.x.war
```

## 2. Deploy to an Application Server

Deploy the WAR file to an application server such as Tomcat.

You may want to rename the file so the version number is not part of the context path:

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # For Tomcat's parallel deployment feature
```

## 3. Configure CLASSPATH

Add the `CLASSPATH` environment variable to the application server.

For Tomcat, create (or edit) `${CATALINA_HOME}/bin/setenv.sh`:

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

## 4. Create the Log Configuration File

Create `logback-spring-ecuacion-tool-command-api.xml` in the CLASSPATH directory:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<configuration>

    <!-- appenders -->
    <property name="log-dir" value="/path/to/logs/directory" />
    <property name="loglevel-spring" value="INFO" />
    <include resource="logback-spring-appenders.xml" />
    <include resource="logback-spring-appenders-local.xml" />

    <!-- loggers -->
    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-security" value="INFO" />
    <property name="loglevel-sql" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-for-local.xml" />
    <include resource="logback-spring-loggers-web-for-local.xml" />

</configuration>
```

## 5. Create the Properties File

Create `ecuacion-tool-command-api.properties` in the CLASSPATH directory (it can be empty initially).
Scripts are registered in this file (see [Quick Start](/public/en/article?id=command-api/quickstart) for details).

## System Requirements

- JDK 21 or above
- Linux or macOS (Windows is not supported)
- A Java application server such as Tomcat
