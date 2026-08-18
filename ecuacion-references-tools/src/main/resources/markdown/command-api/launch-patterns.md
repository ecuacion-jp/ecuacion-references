This page describes the two ways to run `ecuacion-tool-command-api`, and what changes between them.

## Standalone

Place the WAR in any directory. It bundles Tomcat, so no external application server is needed.

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

Once started, the API is available at `http://localhost:8080`.

## Deploying to an Existing Tomcat

You can also deploy the WAR file to an application server such as Tomcat. The WAR ships with `META-INF/context.xml` already bundled, which pre-wires a classpath to the `app-conf/ecuacion-tool-command-api` directory — just drop your configuration files there and they're picked up automatically (see [Configuration Files](page?id=command-api/config&lang=en) for details).

You may want to rename the file so the version number is not part of the context path:

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # For Tomcat's parallel deployment feature
```

## What Differs Between the Two

Either way you start it, script registration itself (the `ecuacion-tool-command-api.properties` format) is the same. Configuration file placement, however, differs by startup method: standalone deployments place files next to the WAR, while deploying to an existing Tomcat has no "next to the WAR" location, so files go through the classpath setting bundled with the WAR for `app-conf` (`META-INF/context.xml`) instead (see [Configuration Files](page?id=command-api/config&lang=en)).
