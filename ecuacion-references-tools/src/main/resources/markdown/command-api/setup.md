## 1. Download the WAR File

Download the latest `ecuacion-tool-command-api-x.x.x.war` from
[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tools/releases).

## 2. Start It

### Standalone (recommended)

Place the WAR in any directory. It bundles Tomcat, so no external application server is needed.

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

Once started, the API is available at `http://localhost:8080`.

### Deploying to an existing Tomcat

You can also deploy the WAR file to an application server such as Tomcat.

You may want to rename the file so the version number is not part of the context path:

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # For Tomcat's parallel deployment feature
```

Either way you start it, script registration and configuration file placement follow the same rules (see [Configuration Files](/public/showMarkdown/page?id=command-api/config&lang=en)).

## System Requirements

- JDK 21 or above
- Linux, macOS, or Windows
