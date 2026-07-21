Endpoints mapped under `/api/key/**` require a valid `X-Api-Key` header. This filter chain runs at
`@Order(9)`, after `/api/public/**` (8) and before the catch-all `/api/**` deny rule (10).

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
  public String getExpectedValue(@Nullable String apiKeyId, String presentedApiKey) {
    // Look up the expected value however fits the application: a fixed value from
    // application.properties, a database row keyed by apiKeyId, etc.
    // Return null to reject the request (e.g. unknown apiKeyId).
    return lookUpExpectedValue(apiKeyId);
  }
}
```

Whether the returned value is compared as plain text or as a SHA-256 hash is controlled by
`jp.ecuacion.splib.rest.api-key.mode`; see
[Comparison Modes](/public/showMarkdown/page?id=rest/security/api-key/comparison-modes&lang=en).

If no `SplibApiKeyExpectedValueProvider` bean is registered at all, every request to `/api/key/**` is
rejected — there is no default "no key required" behavior for this prefix, unlike `/api/public/**`.

## Rejection behavior

`SplibApiKeyAuthenticationFilter` rejects with a generic `401` in every case below, so a caller cannot
tell which one occurred:

- The `X-Api-Key` header is missing or empty.
- No `SplibApiKeyExpectedValueProvider` bean is registered.
- The provider returns `null` (e.g. unknown `apiKeyId`).
- The presented key does not match the expected value.

Details are logged server-side only; the presented key value itself is never logged. The comparison
uses `MessageDigest.isEqual` (constant-time) to avoid a timing attack.

## On success

A successful match authenticates the request as `apiKeyId` (or `"api-key-client"` if no
`X-Api-Key-Id` was sent) with the `ROLE_API_KEY` authority.

## Why CSRF is disabled here too

Unlike a typical cookie-authenticated endpoint, CSRF exploits *ambient* credentials — ones the
browser attaches automatically without the page's JavaScript needing to know their value.
`X-Api-Key` is not ambient: a cross-site page cannot set it without already knowing the key, and by
then it could call the API directly without needing the victim's browser at all. So there is nothing
for CSRF protection to add here, regardless of whether the endpoint underneath is read-only —
contrast this with
[Public Endpoints](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=en), where CSRF is
safe to disable only *because* the convention is read-only.
