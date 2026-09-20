Requests fall through the built-in `/api/public/**` / `/api/ecuacion-splib/public/**` (11),
`/api/key/**` (12), `/api/ecuacion-splib/key/**` (13), and finally the `/api/**` deny-all (14)
chains, in that order. The fourth chain, at `@Order(14)` with `securityMatcher("/api/**")`, denies
every request that reaches it — `anyRequest().denyAll()`. This is the catch-all for anything under
`/api/**` that is not `/api/public/**`, `/api/ecuacion-splib/public/**`, `/api/key/**`, or
`/api/ecuacion-splib/key/**`. (`ecuacion-splib-rest`'s chains reserve `@Order` values `11`-`19`;
see `SplibRestSecurityConfig`'s javadoc.)

A fifth chain, with `securityMatcher("/**")` and no explicit `@Order` (so it runs dead last), denies
every request that reaches *it* too — this is the catch-all for anything **outside** `/api/**`.
Registering any custom `SecurityFilterChain` bean disables Spring Boot's own default security chain
entirely, so without this fifth chain, a path matching none of the other four (e.g. an endpoint
added later outside `/api/**`, or one where the `/api` prefix was simply forgotten) would bypass
Spring Security altogether — not denied, just never checked. This chain makes "denied" the default
instead.

## Adding your own security policy

To expose an endpoint under a path other than the built-in prefixes (e.g. anything not starting
with `/api/`), register your own `SecurityFilterChain` bean with a `securityMatcher` for that path.
Any explicit, finite `@Order` works — it's automatically evaluated before the unordered `/**`
catch-all described above, so your chain decides that path's policy instead of the catch-all's
unconditional deny.

```java
@Configuration
public class AppCustomApiSecurityConfig {

  @Order(100)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/custom/**");

    // configure authentication/authorization for this path here

    return http.build();
  }
}
```

> If your application also uses `ecuacion-splib-web`, its `SplibWebSecurityConfig` already plays
> this same catch-all role for non-`/api` paths (at `@Order(29)`) — see
> [Form Login & Access Control](page?id=web/security/form-login&lang=en). Register a custom chain
> like the one above only for a path you want to carve out a *different* policy for than that
> class's default `permitAll`/role-based rules.

To instead apply a different policy to a sub-path of `/api/**`, register your own
`SecurityFilterChain` bean with `@Order` **lower than 11** — Spring Security evaluates filter
chains in ascending `@Order` and stops at the first `securityMatcher` that matches the request, so
your chain must be checked before the built-in ones, including the `/api/**` catch-all deny-all at
`@Order(14)`.

```java
  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // configure authentication/authorization for this path here

    return http.build();
  }
```
