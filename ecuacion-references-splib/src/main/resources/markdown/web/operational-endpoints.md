`ecuacion-splib-web` provides one built-in controller, `ConfigController`, under
`/ecuacion-splib/admin/config`, protected by
[Built-in Admin Authentication](page?id=web/security/builtin-admin&lang=en) — since both of its
actions have side effects, it lives under `/ecuacion-splib/admin/**` rather than a `permitAll`
path.

## `ConfigController`

```
GET  /ecuacion-splib/admin/config/page
POST /ecuacion-splib/admin/config/action?action=clearPropertiesCache
POST /ecuacion-splib/admin/config/action?action=systemError
```

### Clear properties cache

Clears the cache of properties files read via `PropertiesFileUtil`, so that changes to
`application.properties` can be picked up without restarting the app. When
`spring-cloud-context` is on the classpath, it also refreshes Spring's own `Environment` (the
cache backing `@Value` / `@ConfigurationProperties`). See
[application.properties](page?id=core/config/application-properties&lang=en) under **core** for
the full mechanism, including the known limitation around multi-name `spring.config.name` setups —
this action is the web-side equivalent of `ecuacion-splib-rest`'s
`POST /api/ecuacion-splib/key/clearPropertiesCache`, and both share the same underlying
`SplibPropertiesCacheClearer`.

### Deliberate system error

Throws a plain `RuntimeException`, so that system-error behavior (exception handling, logging,
and so on — see [Exception Handling](page?id=web/exception-handling&lang=en)) can be tested
without requiring an actual bug.
