Endpoints mapped under `/api/public/**` are always reachable — `permitAll`, no authentication
required. This filter chain runs at `@Order(8)`, the first of the three chains
`SplibRestSecurityConfig` registers.

## Keep `/api/public/**` to what's safe to make public

`/api/public/**` is reachable without authentication (`permitAll`). Anyone, from anywhere, can call it
directly, so only put things here — data or actions — that are **safe to expose publicly**.

An endpoint with side effects (a write) is almost never safe to make public, so in practice this prefix
ends up being read-only (GET/HEAD only). Nothing in `ecuacion-splib-rest` enforces that, so if an
endpoint needs to write data, put it under `/api/key/**` (see
[Key Endpoints](page?id=rest/security/api-key/overview&lang=en)) or
behind your own security configuration (see
[Custom Endpoint Security](page?id=rest/security/custom-endpoints&lang=en)).

## `/api/ecuacion-splib/public/**` is reserved for `ecuacion-splib`'s own endpoints

The same filter chain also permits `/api/ecuacion-splib/public/**`, with the identical `permitAll`
policy. This prefix is reserved for `ecuacion-splib`'s own built-in endpoints that are safe to
expose without authentication — currently just the
[Alive Check Endpoint](page?id=rest/alive-check-endpoint&lang=en)
(`GET`/`POST /api/ecuacion-splib/public/aliveCheck`) — so that `/api/public/**` stays exclusively
the application's own namespace. Built-in endpoints with side effects, such as the
[Operational Endpoints](page?id=rest/operational-endpoints&lang=en)
(`POST /api/ecuacion-splib/key/clearPropertiesCache`, `POST /api/ecuacion-splib/key/systemError`),
instead live under `/api/ecuacion-splib/key/**`; see
[Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en).
