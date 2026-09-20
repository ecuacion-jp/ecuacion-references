As shown in [Quickstart](page?id=cli/quickstart&lang=en) step 4, `SplibCliApplication` prints its
own startup banner in place of Spring Boot's default one — nothing to configure for that on its
own:

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)
-----
```

## Showing your app's own name

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
-----
```

## Showing your app's own version

The version shown alongside it is read via `VersionUtil.getVersion("")`, so add
`src/main/resources/version.properties`:

```properties
version=${project.version}
```

Once the version shows up, one more line is added below the name.

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)

       app  my-app
                            v1.0.0
-----
```

`${project.version}` is replaced with the actual build version via Maven resource filtering. Add a
`<resources>` config to your pom.xml that filters only `version.properties`, to keep `${...}` in
other resources — e.g. logback configs — untouched.

```xml
<build>
  <resources>
    <resource>
      <directory>src/main/resources</directory>
      <filtering>false</filtering>
    </resource>
    <resource>
      <directory>src/main/resources</directory>
      <includes>
        <include>version.properties</include>
      </includes>
      <filtering>true</filtering>
    </resource>
  </resources>
</build>
```

See
[`ecuacion-tool-code-generator-core`'s `pom.xml`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator)
for a working example. That example uses `@project.version@` instead of `${project.version}`,
since its parent POM is `ecuacion-splib-parent` (see [Setup](page?id=home&lang=en) on the home
page).

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
