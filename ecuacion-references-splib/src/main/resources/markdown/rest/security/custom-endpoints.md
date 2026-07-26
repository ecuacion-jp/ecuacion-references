Every request that reaches the third filter chain registered by `SplibRestSecurityConfig`
(`@Order(10)`, `securityMatcher("/api/**")`) is denied — `anyRequest().denyAll()`. This is the
catch-all for anything under `/api/**` that is not `/api/public/**`, `/api/ecuacion/public/**`, or
`/api/key/**`.

## Adding your own security policy

To expose an endpoint under a path other than the built-in prefixes, or to apply a different
policy to a sub-path of `/api/**` (session-based authentication, a different header scheme, and so
on), register your own `SecurityFilterChain` bean with `@Order` **lower than 8** — Spring Security
evaluates filter chains in ascending `@Order` and stops at the first `securityMatcher` that matches
the request, so your chain must be checked before the built-in ones.

```java
@Configuration
public class AppCustomApiSecurityConfig {

  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // configure authentication/authorization for this path here

    return http.build();
  }
}
```

Requests that don't match your custom `securityMatcher` fall through to the built-in
`/api/public/**` / `/api/ecuacion/public/**` (8), `/api/key/**` (9), and finally the `/api/**`
deny-all (10) chains, in that order.

## Reserved orders

`ecuacion-splib-rest` uses `@Order(8)`, `@Order(9)`, and `@Order(10)` for
[Public Endpoints](page?id=rest/security/public-endpoints&lang=en),
[API Key Authentication](page?id=rest/security/api-key/overview&lang=en), and the
deny-all rule, respectively. Keep application-defined chains outside this range (below 8, since the
deny-all rule at 10 must remain the final fallback).
