`ecuacion-splib-rest` is the ecuacion-splib module for building REST APIs. It builds on
`ecuacion-splib-core` and adds the pieces a REST API needs on top of it: a URL-prefix-based security
convention, an API key authentication mechanism, and a common exception handler.

## What it provides

- **Endpoint-prefix security convention** — every endpoint is placed under one of three prefixes
  (`/api/public/**`, `/api/key/**`, `/api/**`), each with a fixed security policy. See
  [Public Endpoints](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=en) and
  [API Key Authentication](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=en).
- **API key authentication** — header-based authentication (`X-Api-Key`) for machine-to-machine
  calls, with a pluggable lookup and a plain/hashed comparison mode.
- **Common exception handling** — uncaught exceptions and a dedicated `HttpStatusException` are
  translated into HTTP responses uniformly. See
  [Exception Handling](/public/showMarkdown/page?id=rest/exception-handling&lang=en).

## URL prefix conventions

| Prefix | Security policy | CSRF |
| --- | --- | --- |
| `/api/public/**` | Always allowed (`permitAll`) | Disabled |
| `/api/key/**` | Requires a valid `X-Api-Key` header | Disabled |
| `/api/**` (anything else) | Always denied (`denyAll`) | N/A |

These three policies are wired up by `SplibRestSecurityConfig`, an abstract class your application
extends. See [Setup](/public/showMarkdown/page?id=rest/setup&lang=en).

## Dependencies

`ecuacion-splib-rest` depends on `ecuacion-splib-core`, and pulls in
`spring-boot-starter-web-services` (a JAX-WS-free variant of Spring MVC's web starter) and
`spring-boot-starter-security`. It does not provide Tomcat itself; add
`spring-boot-starter-tomcat` with `provided` scope in the application, the same as for a WAR-packaged
`ecuacion-splib-web` application.
