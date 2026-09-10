`SplibWebSecurityConfig` and its variants are abstract classes your application extends to wire up
`HttpSecurity` — form login, logout, and role/authority-based `authorizeHttpRequests` rules —
without writing that boilerplate by hand.

## The four variants

| Class | Use case | Login URL prefix |
| --- | --- | --- |
| `SplibWebSecurityConfig` | The application's own end-user login (form login, optionally OAuth2 social login) | `/public/**` |
| `SplibWebSecurityConfigForNoLogin` | Same as above, but for an application with no login at all (`isLoginEnabled()` sealed to `false`) | `/public/**` |
| `SplibWebSecurityConfigForAdmin` | The application's own separate admin login, independent of the end-user login above | `/public/admin*/**`, `/admin/**` |
| `SplibWebSecurityConfigForSwitchUser` | Impersonation ("switch user") for an already-logged-in admin/support user, on top of whichever of the above is in use | (adds `/admin/switchUser`, `/account/exitUser`) |

All four are independent — an application typically extends one of the first two, and may add
`SplibWebSecurityConfigForAdmin` and/or `SplibWebSecurityConfigForSwitchUser` on top depending on
whether it needs a separate admin login and/or impersonation. None of them is registered
automatically; each is a plain `abstract class` your application subclasses and annotates with
`@Configuration` (and `@EnableWebSecurity` on whichever one first enables Spring Security).

## `SplibWebSecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfig {

  public AppSecurityConfig() {
    super(null, null, null); // pass OAuth2 beans here if needed — see "Social login" below
  }

  @Override
  protected String getDefaultSuccessUrl() {
    return "/home/page";
  }

  @Override
  protected String getLoginNeededPage() {
    return "/public/login/page";
  }

  @Override
  protected String getAccessDeniedPage() {
    return "/public/login/page?accessDenied";
  }

  @Override
  protected List<AuthorizationBean> getRoleInfo() {
    return List.of(new AuthorizationBean("/admin/**", "ADMIN"));
  }

  @Override
  protected List<AuthorizationBean> getAuthorityInfo() {
    return List.of();
  }
}
```

Form login is wired to `POST /public/login/action` (username/password parameter names
`login.username` / `login.password`), and logout to `POST /public/logout`. `/public/**`,
`/ecuacion-splib/public/**`, and static resources are always `permitAll`; every other path is
`denyAll` unless it matches one of the `AuthorizationBean` entries returned by `getRoleInfo()` /
`getAuthorityInfo()`.

### The reserved `ACCOUNT_FULL_ACCESS` role

`/account/**` is automatically granted to a reserved role, `ACCOUNT_FULL_ACCESS`, in addition to
whatever `getRoleInfo()` returns — useful for a group administrator or power-user role that needs
full access to that area without listing every sub-path by hand.

### Social login (Google / Apple)

To enable OAuth2 social login, register a `SplibOauth2UserHandler` bean and pass a
`SplibOauth2AuthSuccessHandler` (and, for Apple, a `SplibAppleClientSecretService`) to the
constructor above. The client registrations themselves are then configured via Spring Security's
own `spring.security.oauth2.client.*` properties — `ecuacion-splib-web` adds no properties of its
own for this. Apple's authorization callback is a `POST` (`response_mode=form_post`), so
`SplibWebSecurityConfig` exempts `/login/oauth2/code/*` from CSRF specifically for that callback;
every other path keeps CSRF protection (see
[CSRF & Transaction Token](page?id=web/security/csrf-and-transaction-token&lang=en)).

## `SplibWebSecurityConfigForNoLogin`

For an application with no login at all (this reference site is one), extend this instead —
`getDefaultSuccessUrl()` and `getLoginNeededPage()` are sealed `final` (never called), and only
`getAccessDeniedPage()` must be implemented (it defaults to the `jp.ecuacion.splib.web.home-page`
property's value if left unoverridden). `getRoleInfo()` / `getAuthorityInfo()` default to `null`
(no role-based access control) and may be overridden if the no-login application still needs some
role/authority-gated pages.

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfigForNoLogin {
}
```

## `SplibWebSecurityConfigForAdmin`

For an application that needs a second, independent login for its own admin area (distinct from
the built-in `ecuacion-splib` admin login — see
[Built-in Admin Authentication](page?id=web/security/builtin-admin&lang=en)). Registers its own
`SecurityFilterChain` at `@Order(21)` (matching only `/public/admin*/**` and `/admin/**`), with
form login at `POST /public/adminLogin/action` (`adminLogin.username` / `adminLogin.password`) and
logout at `POST /public/adminLogout`. Like the reserved `ACCOUNT_FULL_ACCESS` role above,
`/admin/**` is automatically granted to a reserved `ADMIN_FULL_ACCESS` role.

## `SplibWebSecurityConfigForSwitchUser`

Adds Spring Security's `SwitchUserFilter` for impersonation — e.g. a support admin logging in as a
specific end user to reproduce their issue. Both actions are `POST`-only, deliberately: this keeps
them CSRF-protected, and keeps the target username out of URLs, access logs, and `Referer`
headers.

```java
@Configuration
public class AppSwitchUserConfig extends SplibWebSecurityConfigForSwitchUser {

  public AppSwitchUserConfig(UserDetailsService userDetailsService) {
    super(userDetailsService);
  }

  @Override
  protected String getSwitchingUserDonePagePath() {
    return "/account/dashboard/page";
  }

  @Override
  protected String getSwitchingUserFailurePagePath() {
    return "/admin/dashboard/page";
  }

  @Override
  protected String getExitingUserDonePagePath() {
    return "/admin/dashboard/page";
  }
}
```

- `POST /admin/switchUser` (parameter `switchUser.username`) — starts impersonating the given
  user, redirecting to `getSwitchingUserDonePagePath()` on success or
  `getSwitchingUserFailurePagePath()` on failure.
- `POST /account/exitUser` — ends impersonation and returns to the original user, redirecting to
  `getExitingUserDonePagePath()` regardless of outcome.

## Secure cookie attribute and TLS-terminating reverse proxies

`ecuacion-splib-web` does not set `server.servlet.session.cookie.secure=true`, nor register a
`ForwardedHeaderFilter` (or enable `server.forward-headers-strategy`), on its own.

In the common deployment where TLS is terminated at a reverse proxy (nginx, an ALB, ...) and the
application itself only ever receives plain HTTP from that proxy, leaving both unset has two
consequences:

- The session cookie is issued without the `Secure` attribute, so nothing stops it from being sent
  over a plaintext connection if one is ever reached (SSL stripping, mixed content, a
  misconfigured client, ...).
- `HttpServletRequest#isSecure()` and redirect URL generation see the connection between the proxy
  and the application — which is plain HTTP — rather than the original client-to-proxy connection,
  even though that one was HTTPS.

If your application is deployed behind such a proxy, set both of the following yourself:

```properties
server.servlet.session.cookie.secure=true
server.forward-headers-strategy=framework
```

The reverse proxy must also be configured to set the `X-Forwarded-Proto` header (or equivalent) —
`server.forward-headers-strategy` only has an effect once that header is actually present and
trusted.
