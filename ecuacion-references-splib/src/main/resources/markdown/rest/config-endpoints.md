`ecuacion-splib-rest` provides two built-in controllers for operational testing, both mapped
under `/api/ecuacion/public/**`:

```
POST /api/ecuacion/public/clearPropertiesCache
POST /api/ecuacion/public/systemError
```

- `ClearPropertiesCacheController` clears the cache of properties files read via
  `PropertiesFileUtil`, so that changes to `application.properties` can be picked up without
  restarting the app.
- `SystemErrorController` deliberately throws a `RuntimeException`, so that the system-error
  behavior (exception handling, logging, and so on) can be tested without requiring an actual
  bug.

Unlike the [Alive Check Endpoint](page?id=rest/alive-check-endpoint&lang=en), both of these have
side effects, so each is rejected with an HTTP `403` response unless
`jp.ecuacion.splib.rest.ecuacion-config-endpoints.enabled` is explicitly set to `true` in
application.properties. Leave it unset (or `false`) in production, and enable it only in
environments where triggering these operational actions publicly is safe.
