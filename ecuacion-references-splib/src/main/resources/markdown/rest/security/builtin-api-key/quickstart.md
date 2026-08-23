If you've set up `AppRestSecurityConfig` from [Quickstart](page?id=rest/quickstart&lang=en), no
extra code is needed to use `/api/ecuacion-splib/key/**` — just set one property.

## 1. Set the key

Unlike `/api/key/**`, there is no application-implemented provider bean here — the expected value
is read directly from `application.properties`. Set exactly one of:

```properties
jp.ecuacion.splib.rest.builtin-api-key.password-plain=your-api-key-here
# or
jp.ecuacion.splib.rest.builtin-api-key.password-bcrypt=$2a$10$...
```

`password-bcrypt` holds a bcrypt hash of the key, so the raw value is never at rest in
`application.properties`. See "Comparison Modes" in
[Authentication Handling](page?id=rest/security/builtin-api-key/authentication&lang=en) for how to
generate one.

If neither is set, every request to `/api/ecuacion-splib/key/**` is rejected — the safe default
for an application that doesn't use these built-in endpoints.

## 2. Try it

Start the application locally and hit it with curl. Try it without the `X-Api-Key` header, or with
the wrong value, too — both should get a `401`.

```
curl -X POST -H "X-Api-Key: your-api-key-here" http://localhost:8080/api/ecuacion-splib/key/clearPropertiesCache
```

A successful call returns an empty `200`. See
[Overview](page?id=rest/security/builtin-api-key/overview&lang=en) for what this built-in
controller does.
