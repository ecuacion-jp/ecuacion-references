`ecuacion-splib-rest` provides two built-in controllers for operational testing, both mapped
under `/api/ecuacion-splib/key/**`:

```
POST /api/ecuacion-splib/key/clearPropertiesCache
POST /api/ecuacion-splib/key/systemError
```

- `ClearPropertiesCacheController` clears the cache of properties files read via
  `PropertiesFileUtil`, so that changes to `application.properties` can be picked up without
  restarting the app. In addition, when `spring-cloud-context` is on the classpath, it also calls
  `ContextRefresher` to refresh Spring's own `Environment` (the property cache backing `@Value` /
  `@ConfigurationProperties`, etc.). When `spring-cloud-context` is absent, this part is skipped
  and an INFO log records that fact.

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
- `SystemErrorController` deliberately throws a `RuntimeException`, so that the system-error
  behavior (exception handling, logging, and so on) can be tested without requiring an actual
  bug.

Unlike the [Alive Check Endpoint](page?id=rest/alive-check-endpoint&lang=en), both of these have
side effects, so neither lives under `/api/ecuacion-splib/public/**`. Instead they require a valid
`X-Api-Key` header, authenticated the same way as [API Key
Authentication](page?id=rest/security/api-key/overview&lang=en) but against a key set that is
registered and rotated independently — see [Built-in Key
Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en). Until your application sets
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` or `...password-bcrypt` in
`application.properties`, every request to `/api/ecuacion-splib/key/**` — including these two
endpoints — is rejected.
