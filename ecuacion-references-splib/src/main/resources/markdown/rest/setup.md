## 1. Add the dependency

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>
```

Add `spring-boot-starter-tomcat` with `provided` scope as well if the application is packaged as a
WAR:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

## 2. Enable `SplibRestConfig`

`SplibRestConfig` component-scans the packages that make `ecuacion-splib-rest` work
(`jp.ecuacion.splib.core.config`, `jp.ecuacion.splib.rest.advice`, `jp.ecuacion.splib.rest.controller`).
Import it from your application configuration:

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 3. Extend `SplibRestSecurityConfig`

`SplibRestSecurityConfig` is an abstract class that wires up the three security filter chains
described in [Overview](/public/showMarkdown/page?id=rest/overview&lang=en). Your application
provides a concrete `@Configuration` subclass:

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

- Pass `null` (or omit registering a `SplibApiKeyExpectedValueProvider` bean) if the application does
  not use `/api/key/**` — every request to that prefix is then rejected. See
  [API Key Authentication](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=en) for how
  to implement the provider.
- No further wiring is needed to get `/api/public/**` (allowed), `/api/key/**` (API-key-gated), and
  everything else under `/api/**` (denied) working.

## 4. (Optional) Handle exceptions from your own actions

Register a bean implementing `SplibExceptionHandlerAction` if you want a side effect (such as sending
an alert email) to run whenever an uncaught exception reaches
`SplibRestExceptionHandler`. See
[Exception Handling](/public/showMarkdown/page?id=rest/exception-handling&lang=en).
