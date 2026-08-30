`ecuacion-splib-web` is the ecuacion-splib module for building server-rendered (Thymeleaf) web
applications. It builds on `ecuacion-splib-core` and adds the pieces a form-based web app needs on
top of it: session-based authentication with role/authority authorization, CSRF and double-submit
protection, a common exception handler, and a few built-in operational features.

> **Scope of this section.** The pages under **web** in this menu cover `ecuacion-splib-web`'s
> internal behavior and security mechanisms only — authentication, CSRF, exception handling, and
> the like. For a hands-on tour of the UI side (controllers, forms, Thymeleaf/Bootstrap
> components), see `ecuacion-splib-web`'s own tutorial application, a separate site built on the
> framework itself rather than a Markdown article collection.

## What it provides

| Feature | Description |
| --- | --- |
| Form login & access control | Session-based form login, OAuth2 (Google/Apple) social login, and role/authority-based `authorizeHttpRequests` rules, wired up by extending `SplibWebSecurityConfig` (or one of its variants for no-login, admin, or impersonation use cases).<br>See [Form Login & Access Control](page?id=web/security/form-login&lang=en). |
| Built-in admin authentication | `ecuacion-splib`'s own `/ecuacion-splib/admin/**` pages (e.g. the operational endpoints below) are protected by a separate, fixed-credential login, independent of the application's own login.<br>See [Built-in Admin Authentication](page?id=web/security/builtin-admin&lang=en). |
| CSRF & double-submit protection | Spring Security's session-based CSRF protection applies by default, and a one-time transaction token layered on top of it catches double form submissions (e.g. double-clicking Submit).<br>See [CSRF & Transaction Token](page?id=web/security/csrf-and-transaction-token&lang=en). |
| Open redirect protection | Exception handlers that redirect back to "the page you came from" derive the target from the `Referer` header, and validate it so it can never send the browser off-site.<br>See [Open Redirect Protection](page?id=web/security/open-redirect-protection&lang=en). |
| Common exception handling | `ViolationException`, `ConstraintViolationException`, and any other uncaught exception are each translated into a page or redirect, with different treatment for each.<br>See [Exception Handling](page?id=web/exception-handling&lang=en). |
| Built-in operational endpoints | A properties-cache-clear action and a deliberate-system-error action, both under the built-in admin login above.<br>See [Operational Endpoints](page?id=web/operational-endpoints&lang=en). |
| Security-conscious logging | Every request is logged at DEBUG with its parameters, with password-looking parameters masked and only the last 8 characters of the session ID recorded.<br>See [Logging](page?id=web/logging&lang=en). |

## Fail-closed defaults

`ecuacion-splib-web` depends on `spring-boot-starter-security` unconditionally, so any application
that pulls it in has Spring Security on its classpath and needs *some* `SecurityFilterChain` to
start up. Two auto-configurations make the safe choice the default one, rather than requiring every
application to opt into it explicitly:

- If no bean extending `SplibWebSecurityConfig` is registered, `SplibWebSecurityAutoConfiguration`
  provides a fallback chain that denies every request (`denyAll`), logged at INFO. See
  [Form Login & Access Control](page?id=web/security/form-login&lang=en) for the real
  configuration this replaces.
- If no `SplibExceptionHandler` bean is registered, `SplibWebExceptionHandlerAutoConfiguration`
  provides a minimal handler for `ViolationException` only. See
  [Exception Handling](page?id=web/exception-handling&lang=en) for the full handler this replaces.

Both fall back to the safe, restrictive behavior rather than silently doing nothing, so a
half-configured application fails loudly (every page denied) rather than quietly serving
unprotected pages.

## Dependencies

`ecuacion-splib-web` depends on `ecuacion-splib-core` and `ecuacion-splib-ui`, and pulls in
`spring-boot-starter-webmvc`, `spring-boot-starter-security`, `spring-boot-starter-oauth2-client`,
and `spring-boot-starter-thymeleaf` (with the layout-dialect and Spring-Security Thymeleaf
extras). It does not provide Tomcat itself; add `spring-boot-starter-tomcat` with `provided`
scope in the application.
