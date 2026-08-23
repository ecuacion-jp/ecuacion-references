Requests fall through the built-in `/api/public/**` / `/api/ecuacion-splib/public/**` (8),
`/api/key/**` (9), `/api/ecuacion-splib/key/**` (10), and finally the `/api/**` deny-all (11)
chains, in that order. The fourth chain, at `@Order(11)` with `securityMatcher("/api/**")`, denies
every request that reaches it — `anyRequest().denyAll()`. This is the catch-all for anything under
`/api/**` that is not `/api/public/**`, `/api/ecuacion-splib/public/**`, `/api/key/**`, or
`/api/ecuacion-splib/key/**`.

## Adding your own security policy

To expose an endpoint under a path other than the built-in prefixes (e.g. anything not starting
with `/api/`), register your own `SecurityFilterChain` bean with a `securityMatcher` for that path.
Any `@Order` works — none of `SplibRestSecurityConfig`'s four chains match a path outside
`/api/**`, so your chain can never collide with them.

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

To instead apply a different policy to a sub-path of `/api/**`, register your own
`SecurityFilterChain` bean with `@Order` **lower than 8** — Spring Security evaluates filter
chains in ascending `@Order` and stops at the first `securityMatcher` that matches the request, so
your chain must be checked before the built-in ones, including the `/api/**` catch-all deny-all at
`@Order(11)`.

```java
  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // configure authentication/authorization for this path here

    return http.build();
  }
```
