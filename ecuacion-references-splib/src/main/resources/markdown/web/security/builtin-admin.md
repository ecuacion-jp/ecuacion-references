`ecuacion-splib-web` provides its own admin pages — currently the
[Operational Endpoints](page?id=web/operational-endpoints&lang=en) — protected by
`SplibBuiltinAdminSecurityConfig`, a concrete, automatically-registered security config
independent of whatever `UserDetailsService` or login the application itself uses.

Unlike `SplibWebSecurityConfigForAdmin` (see
[Form Login & Access Control](page?id=web/security/form-login&lang=en)), which is abstract and
must be subclassed, this class needs no application code at all — only the credential property
below.

## URL prefixes

`SplibBuiltinAdminSecurityConfig` registers its own `SecurityFilterChain` at `@Order(22)`, matching
only `/ecuacion-splib/public/adminLogin/**`, `/ecuacion-splib/admin/**`, and
`/ecuacion-splib/adminLogout`. `/ecuacion-splib/public/adminLogin/**` is `permitAll` (it's the
login page itself); everything else under `/ecuacion-splib/admin/**` requires authentication.

## Credential property

Set exactly one of the following in `application.properties`:

| Property | Meaning |
| --- | --- |
| `jp.ecuacion.splib.web.builtin-admin-login.password-plain` | The password itself, in plain text. |
| `jp.ecuacion.splib.web.builtin-admin-login.password-bcrypt` | A bcrypt hash of the password, generated the same way as for a stored user password (e.g. `new BCryptPasswordEncoder().encode(rawPassword)`). |

The fixed username is always `ecuacion-splib` (`SplibBuiltinAdminSecurityConfig.BUILTIN_ADMIN_USERNAME`).
Both properties are re-resolved on every login attempt (not cached at startup), so a value changed
via [Operational Endpoints](page?id=web/operational-endpoints&lang=en)'s cache-clear action takes
effect immediately, without a restart.

## Fails closed, not open

If **neither** property is set, no login is possible at all — indistinguishable from a wrong
password — which is the safe default for an application that doesn't use this feature. If
**both** are set, that's instead a misconfiguration only whoever controls
`application.properties` could cause, so it's surfaced distinctly: the login page shows a
dedicated "credentials misconfigured" message (`?credentialMisconfigured`) rather than the generic
wrong-credentials one (`?error`), so it isn't mistaken for someone simply mistyping the password.

This mirrors how `ecuacion-splib-rest`'s equivalent built-in API key tier
(`/api/ecuacion-splib/key/**`) rejects every request when neither of its own credential properties
is set.

## Login / logout

| Action | URL |
| --- | --- |
| Login page | `GET /ecuacion-splib/public/adminLogin/page` |
| Login submit | `POST /ecuacion-splib/public/adminLogin/action` (`builtinAdminLogin.username` / `builtinAdminLogin.password`) |
| Logout | `POST /ecuacion-splib/adminLogout` |

A successful login lands on `/ecuacion-splib/admin/config/page` and is granted the
`ROLE_BUILTIN_ADMIN` authority — used only internally by `ecuacion-splib-web`'s own controllers,
not something an application is expected to check itself.
