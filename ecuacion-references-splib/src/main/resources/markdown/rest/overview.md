`ecuacion-splib-rest` is the ecuacion-splib module for building REST APIs. It builds on
`ecuacion-splib-core` and adds the pieces a REST API needs on top of it: a URL-prefix-based security
convention, an API key authentication mechanism, and a common exception handler.

## What it provides

- **Endpoint-prefix security convention** — every endpoint is placed under one of four security
  policies (`/api/public/**`, `/api/ecuacion-splib/key/**`, `/api/key/**`, `/api/**`).
  `/api/ecuacion-splib/public/**` shares the `/api/public/**` policy but is reserved for
  `ecuacion-splib`'s own built-in endpoints that are safe to expose without authentication. See
  [Public Endpoints](page?id=rest/security/public-endpoints&lang=en),
  [Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en), and
  [API Key Authentication](page?id=rest/security/api-key/overview&lang=en).
- **API key authentication** — header-based authentication (`X-Api-Key`) for machine-to-machine
  calls, with a pluggable lookup and a plain/hashed comparison mode. `ecuacion-splib`'s own
  built-in endpoints with side effects use an independently-registered key set, kept separate from
  the application's `/api/key/**` keys.
- **Common exception handling** — uncaught exceptions and a dedicated `HttpStatusException` are
  translated into HTTP responses uniformly. See
  [Exception Handling](page?id=rest/exception-handling&lang=en).

## URL prefix conventions

| Prefix | Security policy |
| --- | --- |
| `/api/public/**` | Always allowed (`permitAll`) |
| `/api/ecuacion-splib/public/**` | Always allowed (`permitAll`) — reserved for `ecuacion-splib`'s own built-in endpoints that are safe to expose without authentication |
| `/api/ecuacion-splib/key/**` | Requires a valid `X-Api-Key` header — for `ecuacion-splib`'s own built-in endpoints with side effects |
| `/api/key/**` | Requires a valid `X-Api-Key` header — the application's own keys |
| `/api/**` (anything else) | Always denied (`denyAll`) |

These four policies are wired up by `SplibRestSecurityConfig`, an abstract class your application
extends. See [Quickstart](page?id=rest/quickstart&lang=en).

## A note on CSRF

CSRF protection is disabled on every one of these paths. CSRF protection only matters when
authorization depends on a credential the browser attaches automatically (e.g. a session cookie);
`/api/public/**` requires no authentication at all, and `/api/key/**` is authenticated via the
`X-Api-Key` header, which is not ambient (the browser never attaches it on its own) — neither path
relies on that premise.

## Dependencies

`ecuacion-splib-rest` depends on `ecuacion-splib-core`, and pulls in
`spring-boot-starter-web-services` (a JAX-WS-free variant of Spring MVC's web starter) and
`spring-boot-starter-security`. It does not provide Tomcat itself; add
`spring-boot-starter-tomcat` with `provided` scope in the application.
