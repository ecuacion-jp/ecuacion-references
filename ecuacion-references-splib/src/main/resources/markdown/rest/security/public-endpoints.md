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

## `/api/ecuacion/public/**` is reserved for `ecuacion-splib`'s own endpoints

The same filter chain also permits `/api/ecuacion/public/**`, with the identical `permitAll`
policy. This prefix is reserved for endpoints `ecuacion-splib` itself provides — the built-in
[Alive Check Endpoint](page?id=rest/alive-check-endpoint&lang=en) (`GET /api/ecuacion/public/aliveCheck`)
and the [Config Endpoints](page?id=rest/config-endpoints&lang=en)
(`POST /api/ecuacion/public/clearPropertiesCache`, `POST /api/ecuacion/public/systemError`,
disabled by default) — so that `/api/public/**` stays exclusively the application's own
namespace.
