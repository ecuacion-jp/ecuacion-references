Assuming the dependency from [Setup](page?id=web/setup&lang=en) is in place, this page wires up
the required configuration classes and confirms the application starts up with a real security
policy, using `ecuacion-splib-web`'s own built-in admin page as the thing to reach — no
application-specific controller or page needed yet.

## 1. Enable `ecuacion-splib-web`'s configuration classes

Component-scan `jp.ecuacion.splib.web.config` from your application configuration. This picks up,
among others, `SplibWebConfig` (which registers the built-in controllers, including the
[Operational Endpoints](page?id=web/operational-endpoints&lang=en) used below to confirm things
work).

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config")
public class AppConfig {
}
```

## 2. Extend `SplibWebSecurityConfig`

Every application needs a bean extending `SplibWebSecurityConfig` — without one,
[Overview](page?id=web/overview&lang=en)'s fail-closed fallback denies every request. If the
application has no login of its own yet, `SplibWebSecurityConfigForNoLogin` is the quickest way to
get a real (if permissive) security policy in place:

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfigForNoLogin {
}
```

See [Form Login & Access Control](page?id=web/security/form-login&lang=en) for the full
`SplibWebSecurityConfig` (form login, OAuth2, role/authority rules) once the application is ready
for real authentication.

## 3. Set a built-in admin credential

`ecuacion-splib-web`'s own admin pages (see
[Built-in Admin Authentication](page?id=web/security/builtin-admin&lang=en)) need exactly one of
these two properties set:

```properties
jp.ecuacion.splib.web.builtin-admin-login.password-plain=change-me
```

## 4. Confirm it

Start the application locally and open the built-in admin login page:

```
http://localhost:8080/ecuacion-splib/public/adminLogin/page
```

Log in with username `ecuacion-splib` and the password set in step 3. A successful login lands on
`/ecuacion-splib/admin/config/page` — see
[Operational Endpoints](page?id=web/operational-endpoints&lang=en) for what that page can do.
