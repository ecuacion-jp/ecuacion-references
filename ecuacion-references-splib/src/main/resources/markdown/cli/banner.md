As shown in [Quickstart](page?id=cli/quickstart&lang=en) step 4, `SplibCliApplication` prints its
own startup banner in place of Spring Boot's default one — nothing to configure for that on its
own:

```
= ecuacion  command line interface
                            v5.0.0
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
                            v5.0.0
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

## Changing or turning off the banner's colors

The `jp.ecuacion.splib.cli.banner-mode` property (default: `color`) accepts:

- `color` — each block in its own color, as shown above.
- `white` — every character in plain white, for a terminal with a dark background where the
  default colors don't read well.
- `black` — every character in plain black, for a terminal with a light background where the
  default colors don't read well.
- `off` — prints nothing.

Set it in `application.properties` to fix a mode for your app:

```properties
jp.ecuacion.splib.cli.banner-mode=white
```

Since command-line arguments always take precedence over `application.properties` (per Spring
Boot's own property source order), an individual user can still override that default at run
time — handy since not everyone's terminal uses the same color scheme:

```
java -jar your-app.jar --jp.ecuacion.splib.cli.banner-mode=black
```
