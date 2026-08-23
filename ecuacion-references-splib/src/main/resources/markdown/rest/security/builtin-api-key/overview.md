`ecuacion-splib-rest` provides two built-in controllers under the `/api/ecuacion-splib/key/**`
prefix, for operational testing.

A valid `X-Api-Key` header is required, authenticated the same way as
[API Key Authentication](page?id=rest/security/api-key/overview&lang=en), but using a key set
reserved for `ecuacion-splib`'s own built-in endpoints with side effects. Unlike the
[Built-in Public Endpoint](page?id=rest/alive-check-endpoint&lang=en), these have side effects, so
they don't live under `/api/ecuacion-splib/public/**`.

See [Quickstart](page?id=rest/security/builtin-api-key/quickstart&lang=en) for the shortest path
to an actual working call, or
[Authentication Handling](page?id=rest/security/builtin-api-key/authentication&lang=en) for how
the authentication mechanism works.

## ClearPropertiesCacheController

```
POST /api/ecuacion-splib/key/clearPropertiesCache
```

Clears the cache of properties files read via `PropertiesFileUtil`, so that changes to
`application.properties` can be picked up without restarting the app. In addition, when
`spring-cloud-context` is on the classpath, it also calls `ContextRefresher` to refresh Spring's
own `Environment` (the property cache backing `@Value` / `@ConfigurationProperties`, etc.). When
`spring-cloud-context` is absent, this part is skipped and an INFO log records that fact.

**Known limitation (as of `spring-cloud-context` 5.0.1)**: this `ContextRefresher`-based refresh
only reliably picks up changes to `application.properties` itself (the *primary*
`spring.config.name`) — verified working across a Spring Boot executable WAR (`java -jar
xxx.war`), a normal deployment to an external Tomcat, and a flat classpath launch alike, so this
is not a classloader or packaging issue. It does **not**, however, pick up changes to property
files loaded via any *additional* name in a multi-name `spring.config.name` (e.g.
`spring.config.name=application,my-app`) — `my-app.properties` changes are never re-read by
`ContextRefresher`, consistently, regardless of deployment style. This appears to be a
limitation in how `ContextRefresher` merges re-loaded property sources back into the running
`Environment` for non-primary config names, not something `ecuacion-splib` can work around.

## SystemErrorController

```
POST /api/ecuacion-splib/key/systemError
```

Deliberately throws a `RuntimeException`, so that the system-error behavior (exception handling,
logging, and so on) can be tested without requiring an actual bug.
