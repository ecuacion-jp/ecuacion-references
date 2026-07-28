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
  public Collection<String> getExpectedValues(@Nullable String apiKeyId, String presentedApiKey) {
    // Look up the expected value(s) however fits the application: fixed values from
    // application.properties, database rows keyed by apiKeyId, etc.
    // Return null or an empty collection to reject the request (e.g. unknown apiKeyId).
    return lookUpExpectedValues(apiKeyId);
  }
}
```

More than one valid value can be returned for a single `apiKeyId` — e.g. one per issued token — so a
single leaked or retired key can be dropped without invalidating the others. The request is
authenticated if `presentedApiKey` matches any of the returned values.

Whether the returned values are compared as plain text or as a SHA-256 hash is controlled by
`jp.ecuacion.splib.rest.api-key.mode`; see
[Comparison Modes](page?id=rest/security/api-key/comparison-modes&lang=en).

If no `SplibApiKeyExpectedValueProvider` bean is registered at all, every request to `/api/key/**` is
rejected — there is no default "no key required" behavior for this prefix, unlike `/api/public/**`.

## Wiring the provider into `AppRestSecurityConfig`

Registering the bean above is not enough by itself. If you followed
[Quickstart](page?id=rest/quickstart&lang=en), `AppRestSecurityConfig` calls
`super(null, null)`, so `/api/key/**` keeps rejecting everything regardless of whether a provider
bean exists. Accept the provider in the constructor and forward it instead:

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

Spring injects the bean registered above automatically. The parameter stays `@Nullable` so the
application still starts even without such a bean — in which case `/api/key/**` keeps rejecting
everything, same as the Quickstart default. The second parameter is unrelated to `/api/key/**`
itself — it backs `/api/ecuacion-splib/key/**` instead; see
[Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en). Pass `null` for
it here if your application doesn't need those built-in endpoints enabled.

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
