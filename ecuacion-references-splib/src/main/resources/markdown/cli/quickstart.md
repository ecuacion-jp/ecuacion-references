Assuming the dependency from [Setup](page?id=cli/setup&lang=en) is in place, this page writes a
minimal app and runs it.

## 1. Write the application's main class

`SplibCliApplication` provides the `main` method logic every CLI app needs (running
`SpringApplication`, running your `SplibCliRunner`, and exiting with the right code), but it
cannot be the class Java actually launches — the `java` command does not follow a `main` method
inherited from a parent class. Your application therefore needs its own class with its own `main`
method that simply delegates:

```java
@SpringBootApplication
public class CliApplication {

  public static void main(String[] args) {
    SplibCliApplication.main(CliApplication.class, args);
  }
}
```

No `@ComponentScan` is needed here — unlike `ecuacion-splib-batch`, `ecuacion-splib-cli`'s own
beans (`SplibExceptionHandler`) register themselves via Spring Boot auto-configuration.

See [Banner](page?id=cli/banner&lang=en) for showing your own app's name/version in the startup
banner, or turning the banner off entirely.

## 2. Keep the console quiet (recommended)

A CLI app's console is its UI, watched directly by the person running it. Logging can be
configured the same way as in any other Spring Boot app, via your own
`src/main/resources/logback-spring.xml` — but raw logback-formatted framework log lines tend to
look out of place mixed into that UI, so we recommend turning logging off entirely and printing
only what you deliberately want the user to see:

```xml
<configuration>
    <root level="OFF" />
</configuration>
```

See [Exception Handling](page?id=cli/exception-handling&lang=en) for how a user can still get a
stack trace on demand despite this quiet default.

## 3. Implement `SplibCliRunner`

```java
@Component
public class HelloRunner implements SplibCliRunner {

  @Override
  public void execute(String[] args) {
    System.out.println("Hello, world!");
  }
}
```

An app provides exactly one `SplibCliRunner` bean — unlike `ecuacion-splib-batch`'s Job/Step split
for unattended execution, a CLI app is watched directly by the person running it, so there's no
need for that kind of division.

## 4. Run it

```
mvn spring-boot:run
```

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)
-----

[2026-08-15 15:37:42] Starting.
Hello, world!
[2026-08-15 15:37:42] Completed successfully.
```

The banner is `SplibCliApplication`'s own — nothing to configure for the default shown above; see
[Banner](page?id=cli/banner&lang=en) to show your own app's name/version there, or turn it off.
The timestamped "Starting."/"Completed successfully." lines (localized) are printed automatically
around your `execute` call — nothing to call for these either.

While `execute` is running, an animated "Running..." indicator (localized; see
[Overview](page?id=cli/overview&lang=en)) is shown on the last console line, spinner to the right
of the text, and cleared once `execute` returns — automatic, nothing to call. It's skipped
entirely when the output isn't an interactive terminal (e.g. redirected to a file), so
redirected/CI output stays clean.

From here: [Exception Handling](page?id=cli/exception-handling&lang=en) covers what happens when
`execute` throws, including how to run your own side effect (such as notifying a developer) on
uncaught exceptions — optional, and not needed to get the app running.
