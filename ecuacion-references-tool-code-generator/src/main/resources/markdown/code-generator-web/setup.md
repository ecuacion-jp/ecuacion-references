# code-generator-web Setup

## 1. Download the WAR

Download the latest `ecuacion-tool-code-generator-web-x.x.x.war` from
[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/releases).

Place the WAR in any directory you prefer. The WAR includes an embedded Tomcat server,
so no external application server is required.

## 2. Configure the Work Directory (optional)

The web module temporarily stores uploaded Excel files and generated output on the server.
The default location is `./app-work`.

To use a different directory, create `application-profile.properties` in the same directory as the WAR
and add the following:

```properties
app.work-root-dir=/path/to/work/directory
```

## System Requirements

- JDK 21 or above
