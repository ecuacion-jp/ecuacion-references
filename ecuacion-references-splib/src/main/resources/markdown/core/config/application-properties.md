`application.properties` is owned entirely by Spring Boot. `PropertiesFileUtil.getApplication(key)`
(and `hasApplication`/`getApplicationOrElse`) delegates directly to Spring's `Environment`, so
it returns exactly the same value `@Value("${key}")` or `Environment.getProperty(key)` would —
including values merged in from an externalized `file:./config/application.properties`,
environment variables, `-D` system properties, and active profiles.

## Which API to use

All three of these return the same value for a key that exists:

```java
@Value("${my.key}")
private String fromValue;

// or, injected as a bean:
env.getProperty("my.key");

// or, ecuacion's own static API:
PropertiesFileUtil.getApplication("my.key");
```

Use `@Value`/`Environment` for ordinary Spring beans. `PropertiesFileUtil.getApplication()`
is for code that also needs to run without a Spring context (`ecuacion-lib-core` itself uses
it that way), or where a static call is more convenient than injecting `Environment`.

## `#{...}` cross-reference is not supported here

Other file kinds let a value reference another key with `#{fileKind:key}` — e.g.
`messages.properties` can embed an `application.properties` value with
`#{application:app.url.root}`. `application.properties` values themselves cannot use this
syntax; it is always left as a plain literal string, even when read through
`PropertiesFileUtil.getApplication()`.

```properties
# Does NOT get resolved — app.url.login is literally "#{application:app.url.root}/login"
app.url.root=https://example.com
app.url.login=#{application:app.url.root}/login
```

This is because `#{...}` is also Spring's own SpEL expression delimiter, used by `@Value`. If
an `application.properties` value contained `#{fileKind:key}` and that same key were ever read
via `@Value`, Spring would try to parse `fileKind:key` as a SpEL expression and fail to start
the application:

```
SpelParseException: Expression [fileKind:key] @...: EL1041E: After parsing a valid
expression, there is still more data in the expression: 'colon(:)'
```

To compose one property's value from another, do it in Java code instead (e.g.
`env.getProperty("app.url.root") + "/login"`) rather than inside `application.properties`.

## Missing keys

`getApplication(key)` throws if the key does not exist anywhere (`application.properties`, an
externalized file, an environment variable, etc.). Use `hasApplication(key)` to check first, or
`getApplicationOrElse(key, default)` for a fallback value.
