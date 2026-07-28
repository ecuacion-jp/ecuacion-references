Endpoints mapped under `/api/ecuacion-splib/key/**` require a valid `X-Api-Key` header, the same
way as [API Key Authentication](page?id=rest/security/api-key/overview&lang=en) — but this prefix
is reserved for `ecuacion-splib`'s own built-in endpoints with side effects, currently the
[Config Endpoints](page?id=rest/config-endpoints&lang=en)
(`ClearPropertiesCacheController`, `SystemErrorController`). This filter chain runs at
`@Order(10)`, after `/api/key/**` (9) and before the catch-all `/api/**` deny rule (11).

## Why a separate key set

`SplibBuiltinApiKeyExpectedValueProvider` is a distinct interface from
`SplibApiKeyExpectedValueProvider`, and `jp.ecuacion.splib.rest.builtin-api-key.mode` is a
separate property from `jp.ecuacion.splib.rest.api-key.mode`, so that the keys guarding
`ecuacion-splib`'s own operational endpoints can be issued, rotated, and revoked independently of
whatever keys your application hands out under `/api/key/**` for its own purposes.

## Request headers

Identical to `/api/key/**`: `X-Api-Key` (required) and `X-Api-Key-Id` (optional). See
[API Key Authentication](page?id=rest/security/api-key/overview&lang=en) for details.

## Implementing the lookup

Register a Spring bean implementing `SplibBuiltinApiKeyExpectedValueProvider`:

```java
@Component
public class AppBuiltinApiKeyExpectedValueProvider
    implements SplibBuiltinApiKeyExpectedValueProvider {

  @Override
  public Collection<String> getExpectedValues(@Nullable String apiKeyId, String presentedApiKey) {
    // Look up the expected value(s) however fits the application: fixed values from
    // application.properties, database rows keyed by apiKeyId, etc.
    // Return null or an empty collection to reject the request (e.g. unknown apiKeyId).
    return lookUpExpectedValues(apiKeyId);
  }
}
```

As with `/api/key/**`, more than one valid value can be returned, the comparison mode
(plain text vs. SHA-256 hash, [Comparison Modes](page?id=rest/security/api-key/comparison-modes&lang=en))
is controlled application-wide — here by `jp.ecuacion.splib.rest.builtin-api-key.mode`
(default `PLAIN`) — and if no provider bean is registered, every request to
`/api/ecuacion-splib/key/**` is rejected.

## Wiring the provider into `AppRestSecurityConfig`

Accept the provider in the constructor and forward it as the second argument to `super`:

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider,
      @Nullable SplibBuiltinApiKeyExpectedValueProvider builtinApiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider, builtinApiKeyExpectedValueProvider);
  }
}
```

Leaving it `null` (the Quickstart default) keeps `/api/ecuacion-splib/key/**` — and so
`clearPropertiesCache`/`systemError` — rejecting everything, same as `/api/key/**` without a
provider.

## Rejection behavior and on success

Identical to `/api/key/**`: a generic `401` for every rejection reason (missing header, no
provider bean, no match, wrong key), with `MessageDigest.isEqual` used for a constant-time
comparison. A successful match authenticates the request with the `ROLE_BUILTIN_API_KEY`
authority (`ROLE_API_KEY` is used for `/api/key/**`).

## About CSRF

Disabled for the same reason as `/api/key/**`: `X-Api-Key` is not an ambient credential the
browser attaches automatically. See [Overview](page?id=rest/overview&lang=en) for details.
