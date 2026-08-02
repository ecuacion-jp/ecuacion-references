Endpoints mapped under `/api/key/**` require a valid `X-Api-Key` header. This filter chain runs at
`@Order(9)`, after `/api/public/**` (8) and before `/api/ecuacion-splib/key/**` (10) and the
catch-all `/api/**` deny rule (11).

## Request headers

| Header | Required | Meaning |
| --- | --- | --- |
| `X-Api-Key` | Yes | The API key itself. |
| `X-Api-Key-Id` | No | An optional key identifier, passed through as-is to your provider — analogous to an AWS access key ID or an HTTP Basic username, used to look up *which* record's expected value to check. A single-shared-key setup can ignore it. |

## Implementing the lookup

Register a Spring bean implementing `SplibApiKeyExpectedValueProvider`:

```java
@Component
public class AppApiKeyExpectedValueProvider implements SplibApiKeyExpectedValueProvider {

  @Override
  public Collection<SplibApiKeyExpectedValue> getExpectedValues(@Nullable String apiKeyId,
      String presentedApiKey) {
    // Look up the expected value(s) however fits the application: fixed values from
    // application.properties, database rows keyed by apiKeyId, etc. Each returned value carries
    // its own SplibApiKeyComparisonMode, so plain-text and bcrypt-hashed values can be mixed.
    // Return null or an empty collection to reject the request (e.g. unknown apiKeyId).
    return lookUpExpectedValues(apiKeyId);
  }
}
```

More than one valid value can be returned for a single `apiKeyId` — e.g. one per issued token — so a
single leaked or retired key can be dropped without invalidating the others. The request is
authenticated if `presentedApiKey` matches any of the returned values.

Each returned `SplibApiKeyExpectedValue` carries its own `SplibApiKeyComparisonMode` (plain text or
bcrypt) rather than a single application-wide setting, so one call can mix both — e.g. while
migrating stored keys from plain text to bcrypt one row at a time. See
[Comparison Modes](page?id=rest/security/api-key/comparison-modes&lang=en).

If no `SplibApiKeyExpectedValueProvider` bean is registered at all, every request to `/api/key/**` is
rejected — there is no default "no key required" behavior for this prefix, unlike `/api/public/**`.

## Wiring the provider into `AppRestSecurityConfig`

Registering the bean above is not enough by itself. If you followed
[Quickstart](page?id=rest/quickstart&lang=en), `AppRestSecurityConfig` calls `super(null)`, so
`/api/key/**` keeps rejecting everything regardless of whether a provider bean exists. Accept the
provider in the constructor and forward it instead:

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

Spring injects the bean registered above automatically. The parameter stays `@Nullable` so the
application still starts even without such a bean — in which case `/api/key/**` keeps rejecting
everything, same as the Quickstart default. This constructor argument is unrelated to
`/api/ecuacion-splib/key/**`, which has no provider bean of its own — see
[Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en) for how that one
is configured instead, via `application.properties`.

## Rejection behavior

`SplibApiKeyAuthenticationFilter` rejects with a generic `401` in every case below, so a caller cannot
tell which one occurred:

- The `X-Api-Key` header is missing or empty.
- No `SplibApiKeyExpectedValueProvider` bean is registered.
- The provider returns `null` or an empty collection (e.g. unknown `apiKeyId`).
- The presented key does not match the expected value.

Details are logged server-side only; the presented key value itself is never logged. The comparison
uses `MessageDigest.isEqual` (constant-time) to avoid a timing attack.

## On success

A successful match authenticates the request as `apiKeyId` (or `"api-key-client"` if no
`X-Api-Key-Id` was sent) with the `ROLE_API_KEY` authority.

## About CSRF

`X-Api-Key` is not an ambient credential the browser attaches automatically, so CSRF is disabled.
See [Overview](page?id=rest/overview&lang=en) for details.
