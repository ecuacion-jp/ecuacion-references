This page describes the two ways to run `ecuacion-tool-command-api`, and what changes between them.

## Standalone

Place the WAR in any directory. It bundles Tomcat, so no external application server is needed.

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

Once started, the API is available at `http://localhost:8080`.

> **Note:** This WAR is built with `spring-boot-maven-plugin`, so standard Spring Boot launch arguments work too — e.g. `--server.port=8081`.

## Deploying to an Existing Tomcat

You can also deploy the WAR file to an application server such as Tomcat. The WAR ships with `META-INF/context.xml` already bundled, which pre-wires a classpath to the `app-conf/ecuacion-tool-command-api` directory — just drop your configuration files there and they're picked up automatically (see [Configuration Files](page?id=command-api/config&lang=en) for details).

You may want to rename the file so the version number is not part of the context path:

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # For Tomcat's parallel deployment feature
```
