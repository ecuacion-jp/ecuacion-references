`ecuacion-splib-web` provides its own admin pages — currently the
[Operational Endpoints](page?id=web/operational-endpoints&lang=en) — protected by
`SplibBuiltinAdminSecurityConfig`, a concrete, automatically-registered security config
independent of whatever `UserDetailsService` or login the application itself uses.

Unlike `SplibWebSecurityConfigForAdmin` (see
[Form Login & Access Control](page?id=web/security/form-login&lang=en)), which is abstract and
must be subclassed, this class needs no application code at all — only the credential property
below.

## HTTP Basic authentication

`SplibBuiltinAdminSecurityConfig` registers its own `SecurityFilterChain` at `@Order(22)`, matching
only `/ecuacion-splib/admin/**`, and protects it with HTTP Basic authentication. There is no login
page: opening a page under `/ecuacion-splib/admin/**` makes the browser show its own credentials
dialog. Enter the fixed username `ecuacion-splib`
(`SplibBuiltinAdminSecurityConfig.BUILTIN_ADMIN_USERNAME`) and the password set below.

An authenticated request is granted the `ROLE_BUILTIN_ADMIN` authority, which the chain requires
for every path it matches. It is used only internally by `ecuacion-splib-web`, not something an
application is expected to check itself.

## Independent of the application's own login

The authentication is neither read from nor stored in the HTTP session, so it never interacts
with the application's own login:

- A user logged in to the application itself is not let in, and is asked for the built-in admin
  credentials just like an anonymous user.
- Authenticating here neither replaces nor logs out the application's own login, and the
  application's own pages never see the built-in admin as a logged-in user.
- The application's own users' credentials (its `UserDetailsService`) are never tried here.
- It works the same in an application that has no login at all (every page public).

Since the browser keeps re-sending the credentials until it is closed, there is no logout. Close
the browser to end it. CSRF protection stays enabled for the same reason.

## Credential property

Set exactly one of the following in `application.properties`:

| Property | Meaning |
| --- | --- |
| `jp.ecuacion.splib.web.builtin-admin-login.password-plain` | The password itself, in plain text. |
| `jp.ecuacion.splib.web.builtin-admin-login.password-bcrypt` | A bcrypt hash of the password, generated the same way as for a stored user password (e.g. `new BCryptPasswordEncoder().encode(rawPassword)`). |

Both properties are re-resolved on every request (not cached at startup), so a value changed via
[Operational Endpoints](page?id=web/operational-endpoints&lang=en)'s cache-clear action takes
effect immediately, without a restart.

The credentials are sent with every request, so serve the application over HTTPS.

## Fails closed, not open

If **neither** property is set, no authentication is possible at all — indistinguishable from a
wrong password (`401`) — which is the safe default for an application that doesn't use this
feature. If **both** are set, that's instead a misconfiguration only whoever controls
`application.properties` could cause, so it's surfaced distinctly: the request is answered with
`500` and an error naming both properties is logged, so it isn't mistaken for someone simply
mistyping the password.

This mirrors how `ecuacion-splib-rest`'s equivalent built-in API key tier
(`/api/ecuacion-splib/key/**`) rejects every request when neither of its own credential properties
is set, and answers `500` when both are.
