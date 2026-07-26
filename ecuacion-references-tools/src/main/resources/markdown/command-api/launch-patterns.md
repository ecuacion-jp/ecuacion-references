This page describes the two ways to run `ecuacion-tool-command-api`, and what changes between them. To get a working instance up as fast as possible, see [Quickstart](page?id=command-api/quickstart&lang=en) instead.

## Standalone (Recommended)

Place the WAR in any directory. It bundles Tomcat, so no external application server is needed.

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

Once started, the API is available at `http://localhost:8080`.

## Deploying to an Existing Tomcat

You can also deploy the WAR file to an application server such as Tomcat.

You may want to rename the file so the version number is not part of the context path:

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # For Tomcat's parallel deployment feature
```

## What Differs Between the Two

Either way you start it, script registration itself (the `ecuacion-tool-command-api.properties` format) is the same. Configuration file placement, however, differs by startup method: standalone deployments place files next to the WAR, while deploying to an existing Tomcat has no "next to the WAR" location, so files go in a directory specified via the `CLASSPATH` environment variable instead (see [Configuration Files](page?id=command-api/config&lang=en)).
