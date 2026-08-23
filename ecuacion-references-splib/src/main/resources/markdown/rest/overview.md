`ecuacion-splib-rest` is the ecuacion-splib module for building REST APIs. It builds on
`ecuacion-splib-core` and adds the pieces a REST API needs on top of it: a URL-prefix-based security
convention, an API key authentication mechanism, and a common exception handler.

## What it provides

| Feature | Description |
| --- | --- |
| Endpoint-prefix security convention | Every endpoint is placed under one of four security policies based on its URL prefix.<br>See the "URL prefix conventions" table below for details, and [Public Endpoints](page?id=rest/security/public-endpoints&lang=en), [Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en), and [API Key Authentication](page?id=rest/security/api-key/overview&lang=en). |
| API key authentication | Header-based authentication (`X-Api-Key`) for machine-to-machine calls, with a pluggable lookup and a plain/hashed comparison mode.<br>`ecuacion-splib`'s own built-in endpoints with side effects use an independently-registered key set, kept separate from the application's `/api/key/**` keys. |
| Common exception handling | `ViolationException`, `ResponseStatusException`, and any other uncaught exception are each translated into an HTTP response, with different treatment for each.<br>See [Exception Handling](page?id=rest/exception-handling&lang=en). |

## URL prefix conventions

<table>
<thead>
<tr><th>Prefix</th><th>Used by</th><th>Security policy</th><th>Notes</th></tr>
</thead>
<tbody>
<tr><td><code>/api/public/**</code></td><td rowspan="2">App</td><td>Always allowed (<code>permitAll</code>)</td><td></td></tr>
<tr><td><code>/api/key/**</code></td><td>Requires a valid <code>X-Api-Key</code> header</td><td>the application's own keys</td></tr>
<tr><td><code>/api/ecuacion-splib/public/**</code></td><td rowspan="2">ecuacion-splib</td><td>Always allowed (<code>permitAll</code>)</td><td>for APIs that are safe to expose without authentication</td></tr>
<tr><td><code>/api/ecuacion-splib/key/**</code></td><td>Requires a valid <code>X-Api-Key</code> header</td><td>for APIs that would be risky to expose without authentication</td></tr>
<tr><td><code>/api/**</code> (anything else)</td><td>—</td><td>Always denied (<code>denyAll</code>)</td><td></td></tr>
</tbody>
</table>

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
