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

### Destination when not logged in

When a page that requires login is accessed without login, the user is redirected to
`getAccessDeniedPage()` by default. To change the destination (for example, to the login page),
override `getAuthenticationEntryPoint()` and return your own `AuthenticationEntryPoint`. Spring
Security saves the original request to its `RequestCache` before calling the entry point, so even
with a replaced entry point, the user returns to the original page after OAuth2 login.

```java
@Override
protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
  return (request, response, authException) -> response
      .sendRedirect(request.getContextPath() + "/public/login/page");
}
```

### Social login (Google / Apple)

To enable OAuth2 social login, extend `SplibOauth2WebSecurityConfig` instead of
`SplibWebSecurityConfig`, pass a `SplibOauth2AuthSuccessHandler`, a `SplibAppleClientSecretService`
(`null` when Apple is not used) and a `ClientRegistrationRepository` to its constructor, and
register a `SplibOauth2UserHandler` bean. The splib beans passed to the constructor
(`SplibOauth2AuthSuccessHandler` and `SplibAppleClientSecretService`) and
`SplibOauth2RegistrationConfirmService` are registered by `SplibOauth2WebSecurityConfig`, so
the `jp.ecuacion.splib.web.oauth2` package does not need to be component-scanned.
`SplibAppleClientSecretService` is registered only when `ecuacion.oauth2.apple.team-id` is set.
The client registrations themselves are then configured via Spring Security's own
`spring.security.oauth2.client.*` properties — `ecuacion-splib-web` adds
no properties of its own for this. Apple's authorization callback is a `POST`
(`response_mode=form_post`), so `SplibOauth2WebSecurityConfig` exempts `/login/oauth2/code/*`
from CSRF specifically for that callback; every other path keeps CSRF protection (see
[CSRF & Transaction Token](page?id=web/security/csrf-and-transaction-token&lang=en)).

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibOauth2WebSecurityConfig {

  public AppSecurityConfig(SplibOauth2AuthSuccessHandler oauth2SuccessHandler,
      @Nullable SplibAppleClientSecretService appleClientSecretService,
      ClientRegistrationRepository clientRegistrationRepository) {
    super(oauth2SuccessHandler, appleClientSecretService, clientRegistrationRepository);
  }

  // implement the same methods as SplibWebSecurityConfig, such as getDefaultSuccessUrl()
}
```

#### Showing a confirm page before registration

`SplibOauth2UserHandler#findOrCreateUser` is expected to register the user on the spot when not
found. To show a confirm page (for example, to agree to the terms of use) before registering,
implement `SplibOauth2UserHandlerWithRegistrationConfirm` instead.

```java
@Component
public class Oauth2UserHandlerImpl implements SplibOauth2UserHandlerWithRegistrationConfirm {

  @Override
  public @Nullable UserDetails findUser(String email, String name, String provider) {
    // returns null when not found (doesn't register)
  }

  @Override
  public String getRegistrationConfirmPage() {
    return "/public/registrationConfirm/page";
  }
}
```

When `findUser` returns `null`, the user is **not** logged in. The information received from the
provider (email address, name and provider) is kept only in the session as
`SplibOauth2PendingRegistration`, and the user is redirected to `getRegistrationConfirmPage()`.
Apple returns the name only on the first authentication, and that name is kept as well. Since the
user is not logged in at this point, the confirm page must be accessible without login (for
example, under `/public/**`).

The confirm page and the following steps use `SplibOauth2RegistrationConfirmService`.

| Method | Purpose |
| --- | --- |
| `getPendingRegistration(request)` | Gets the kept information.<br>`null` if not exists, for example when the session has expired. |
| `discardPendingRegistration(request)` | Discards the kept information (for example, when the user cancels). |
| `loginAfterRegistration(request, response, userDetails)` | Call it after the app has registered the user to log the user in.<br>The kept information is discarded, and the session id is changed and the CSRF token is renewed, as with the regular login.<br>Returns the url to redirect to (the url saved before login, or `getDefaultSuccessUrl()` if not saved). |

```java
@PostMapping("action")
public String register(HttpServletRequest request, HttpServletResponse response) {
  SplibOauth2PendingRegistration pending = service.getPendingRegistration(request);
  // if pending is null, go back to the login page

  UserDetails userDetails = // register the user with the information in pending
  return "redirect:" + service.loginAfterRegistration(request, response, userDetails);
}
```

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
  `getExitingUserDonePagePath()`.

### Deciding the pages per request

To change the pages by where the impersonation is started from, override the methods of the same
names taking an `HttpServletRequest`. They are called on every request, and by default they call
the methods with no arguments.

| Method | Called when |
| --- | --- |
| `getSwitchingUserDonePagePath(request)` | Starting impersonation has succeeded |
| `getSwitchingUserFailurePagePath(request)` | Starting impersonation has failed |
| `getExitingUserDonePagePath(request)` | Impersonation has ended |
| `onSwitchingUserDone(request, response, authentication)` | Starting impersonation has succeeded (just before the redirect).<br>Does nothing by default. |
| `onExitingUserDone(request, response, authentication)` | Impersonation has ended (just before the redirect).<br>Does nothing by default. |

The exit request carries no information about where the impersonation was started from, so keep
the return path in the session when starting and read it when exiting (the session is kept from
start to exit). `onXxxDone` is called after the method deciding the path, so the session values
used to decide it can be removed there.

```java
@Override
protected String getSwitchingUserDonePagePath(HttpServletRequest request) {
  String id = request.getParameter("inquiryId");
  if (id == null || !id.matches("[0-9]+")) {
    return getSwitchingUserDonePagePath();
  }

  request.getSession().setAttribute("switchUserReturnPath", "/admin/inquiry/detail/page?id=" + id);
  return "/account/inquiry/detail/page?id=" + id;
}

@Override
protected String getExitingUserDonePagePath(HttpServletRequest request) {
  return request.getSession().getAttribute("switchUserReturnPath") instanceof String path ? path
      : getExitingUserDonePagePath();
}

@Override
protected void onExitingUserDone(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) {
  request.getSession().removeAttribute("switchUserReturnPath");
}
```

The returned path must be one in the app, starting with `/`. A path starting with `//` or `/\`
can be a url to another site, so it causes an `IllegalStateException`. When building the path from
request parameters, validate the values as in the example above.

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
