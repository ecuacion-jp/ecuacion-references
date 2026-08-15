As shown in [Quickstart](page?id=cli/quickstart&lang=en) step 4, `SplibCliApplication` prints its
own startup banner in place of Spring Boot's default one — nothing to configure for that on its
own:

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)
-----
```

## Showing your app's own name and version

Pass a third argument to `SplibCliApplication.main` instead of the two-argument form:

```java
SplibCliApplication.main(CliApplication.class, args, "my-app");
```

This adds a second block, right above the same separator line:

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)

       app  my-app
                   v1.0.0
-----
```

The version shown alongside it is read via `VersionUtil.getVersion("")`, so your app needs its
own `version.properties` — see
[`ecuacion-tool-code-generator-core`'s `pom.xml`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator)
for a working example of the `version.properties` file and the `<resources>` filtering it needs
(only that one file should be filtered, to keep `${...}`/`@...@` in other resources — e.g.
logback configs — untouched). If your project's parent POM isn't `ecuacion-splib-parent` (see
[Setup](page?id=home&lang=en) on the home page), use plain `${project.version}` in
`version.properties` instead of `@project.version@` — the `@`-delimiter convention comes from
`ecuacion-splib-parent`'s own Maven plugin configuration, which a different parent POM won't
provide.

## Turning the banner off

Set the `jp.ecuacion.splib.cli.banner-mode` property to `off` (default: `on`) — e.g. as a command
line argument:

```
java -jar your-app.jar --jp.ecuacion.splib.cli.banner-mode=off
```

or in `application.properties`:

```
jp.ecuacion.splib.cli.banner-mode=off
```
