Assuming you've already added the dependency from [Setup](page?id=rest/setup&lang=en), this page
adds one minimal, actually-working endpoint under `/api/key/**`.

## 1. Enable `SplibRestConfig`

`SplibRestConfig` (in the `jp.ecuacion.splib.rest.config` package) component-scans the packages
`ecuacion-splib-rest` needs to work (`jp.ecuacion.splib.core.config`, `jp.ecuacion.splib.rest.advice`,
`jp.ecuacion.splib.rest.controller`). Import it from your application's configuration class:

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 2. Implement the lookup

Register a Spring bean implementing `SplibApiKeyExpectedValueProvider`:

```java
@Component
public class AppApiKeyExpectedValueProvider implements SplibApiKeyExpectedValueProvider {

  @Override
  public Collection<SplibApiKeyExpectedValue> getExpectedValues(@Nullable String apiKeyId,
      String presentedApiKey) {
    return List.of(
        new SplibApiKeyExpectedValue("your-api-key-here", SplibApiKeyComparisonMode.PLAIN));
  }
}
```

This is the minimal shape: one fixed key, compared directly. See "Implementing the lookup" in
[Authentication Handling](page?id=rest/security/api-key/authentication&lang=en) for how a real
application would look values up (a database, multiple keys, and so on).

## 3. Extend `SplibRestSecurityConfig`

`SplibRestSecurityConfig` is an abstract class that wires up the four security filter chains
described in [Overview](page?id=rest/overview&lang=en). Provide a concrete `@Configuration`
subclass that accepts the provider from step 2 in its constructor and forwards it:

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

Spring injects the bean registered above automatically. The parameter stays `@Nullable` so the
application still starts even without such a bean — in which case every request to `/api/key/**`
is rejected.

## 4. Write a controller

```java
@RestController
public class HelloKeyController {

  @GetMapping("/api/key/hello")
  public HelloResponse hello() {
    return new HelloResponse("Hello, key!");
  }
}

record HelloResponse(String message) {
}
```

It's under `/api/key/**`, so with steps 2 and 3 in place, only requests carrying a valid
`X-Api-Key` reach it.

## 5. Try it

Start the application locally and hit it with curl. Try it without the `X-Api-Key` header, or with
the wrong value, too — both should get a `401`.

```
curl -H "X-Api-Key: your-api-key-here" http://localhost:8080/api/key/hello
```

```json
{"message":"Hello, key!"}
```
