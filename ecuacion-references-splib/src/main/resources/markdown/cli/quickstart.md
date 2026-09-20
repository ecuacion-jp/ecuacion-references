Assuming the dependency from [Setup](page?id=cli/setup&lang=en) is in place, this page writes a
minimal app and runs it.

## 1. Write the application's main class

Write a class with its own `main` method that delegates to `SplibCliApplication`:

```java
@SpringBootApplication
public class CliApplication {

  public static void main(String[] args) {
    SplibCliApplication.main(CliApplication.class, args);
  }
}
```

## 2. Implement `SplibCliRunner`

This is the only place your app's logic goes. Implement `SplibCliRunner` with a single `execute`
method.

```java
@Component
public class HelloRunner implements SplibCliRunner {

  @Override
  public void execute(String[] args) throws InterruptedException {
    Thread.sleep(3000); // dummy work so you can see the "Running..." indicator
    System.out.println("Hello, world!");
  }
}
```

## 3. Keep the console quiet (recommended: optional)

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

## 4. Run it

Run it and see it work.

```
mvn spring-boot:run
```

You should see output like this.

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)
-----

[2026-08-15 15:37:42] Starting.
Hello, world!
[2026-08-15 15:37:42] Completed successfully.
```
