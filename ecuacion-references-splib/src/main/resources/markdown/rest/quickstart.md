Assuming the dependency from [Setup](page?id=rest/setup&lang=en) is in place,
this page wires up the required configuration classes and adds one endpoint that actually runs.

## 1. Enable `SplibRestConfig`

`SplibRestConfig` (in the `jp.ecuacion.splib.rest.config` package) component-scans the packages that
make `ecuacion-splib-rest` work (`jp.ecuacion.splib.core.config`, `jp.ecuacion.splib.rest.advice`,
`jp.ecuacion.splib.rest.controller`). Import it from your application configuration:

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 2. Extend `SplibRestSecurityConfig`

`SplibRestSecurityConfig` is an abstract class that wires up the three security filter chains
described in [Overview](page?id=rest/overview&lang=en). Your application
provides a concrete `@Configuration` subclass:

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig() {
    super(null);
  }
}
```

This makes `/api/public/**` reachable.

## 3. Write a controller

```java
@RestController
public class HelloController {

  @GetMapping("/api/public/hello")
  public HelloResponse hello() {
    return new HelloResponse("Hello, world!");
  }
}

record HelloResponse(String message) {
}
```

Because it lives under `/api/public/**`, the `SplibRestSecurityConfig` subclass from step 2 already
allows anyone to call it (`permitAll`) with no further configuration.

## 4. Call it

Start the application locally and hit it with curl:

```
curl http://localhost:8080/api/public/hello
```

```json
{"message":"Hello, world!"}
```

This reference site itself uses `ecuacion-splib-rest` and exposes the exact same controller at
[`/api/public/hello`](../../api/public/hello). See `jp.ecuacion.references.splib.tutorial.rest.HelloController`
in the site's own source for the implementation.

From here, [Exception Handling](page?id=rest/exception-handling&lang=en) covers
what happens when a controller throws, including how to run your own side effect (such as sending an
alert email) on uncaught exceptions — optional, and not needed to get an endpoint running.
