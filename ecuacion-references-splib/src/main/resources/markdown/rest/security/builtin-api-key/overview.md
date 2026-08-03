Endpoints mapped under `/api/ecuacion-splib/key/**` require a valid `X-Api-Key` header, the same
way as [API Key Authentication](page?id=rest/security/api-key/overview&lang=en) — but this prefix
is reserved for `ecuacion-splib`'s own built-in endpoints with side effects, currently the
[Operational Endpoints](page?id=rest/operational-endpoints&lang=en)
(`ClearPropertiesCacheController`, `SystemErrorController`). This filter chain runs at
`@Order(10)`, after `/api/key/**` (9) and before the catch-all `/api/**` deny rule (11).

## Why a separate key set

`jp.ecuacion.splib.rest.builtin-api-key.*` is a separate property namespace from
`jp.ecuacion.splib.rest.api-key.*`, so that the key guarding `ecuacion-splib`'s own operational
endpoints can be issued and rotated independently of whatever key your application uses under
`/api/key/**` for its own purposes.

## Request headers

Identical to `/api/key/**`: `X-Api-Key` (required) and `X-Api-Key-Id` (optional, carried through
only as the authenticated principal's name for logging — there is a single fixed key here, not
one per client). See
[API Key Authentication](page?id=rest/security/api-key/overview&lang=en) for details.

## Configuring the key

Unlike `/api/key/**`, there is no application-implemented provider bean here — the expected value
is read directly from `application.properties`. Set exactly one of:

```properties
jp.ecuacion.splib.rest.builtin-api-key.password-plain=your-api-key-here
# or
jp.ecuacion.splib.rest.builtin-api-key.password-bcrypt=$2a$10$...
```

`password-bcrypt` holds a bcrypt hash of the key, so the raw value is never at rest in
`application.properties`. Both are resolved fresh on every request via
`jp.ecuacion.splib.core.util.SplibHashedPropertyResolver`, so clearing the
`PropertiesFileUtil` cache (see [Operational Endpoints](page?id=rest/operational-endpoints&lang=en)) picks
up a changed value without a restart.

- **Neither set:** every request to `/api/ecuacion-splib/key/**` is rejected — the safe default
  for an application that doesn't use these built-in endpoints.
- **Exactly one set:** the presented `X-Api-Key` is compared against it (plain text or bcrypt, as
  configured).
- **Both set:** this is a misconfiguration only whoever controls `application.properties` could
  cause (never an external caller), so it's reported distinctly — see below.

## Rejection behavior and on success

A missing header or a wrong/absent key returns a generic `401` (`MessageDigest.isEqual` is used
for a constant-time comparison), the same as `/api/key/**` — indistinguishable from each other so
a caller can't tell "no such key" from "wrong key". Having *both*
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` and `...password-bcrypt` set at once is
different: it returns a `500` naming the two offending property keys, since that state can only
be reached by whoever controls `application.properties`.

A successful match authenticates the request with the `ROLE_BUILTIN_API_KEY` authority
(`ROLE_API_KEY` is used for `/api/key/**`).

## About CSRF

Disabled for the same reason as `/api/key/**`: `X-Api-Key` is not an ambient credential the
browser attaches automatically. See [Overview](page?id=rest/overview&lang=en) for details.
