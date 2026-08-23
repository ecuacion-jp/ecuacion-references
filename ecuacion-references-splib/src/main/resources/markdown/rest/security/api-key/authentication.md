The `/api/key/**` authentication described in
[Overview](page?id=rest/security/api-key/overview&lang=en) is driven by the
`SplibApiKeyExpectedValueProvider` implementation your application registers.

## Implementing the lookup

Register a Spring bean implementing `SplibApiKeyExpectedValueProvider`. See
[Quickstart](page?id=rest/security/api-key/quickstart&lang=en) for how to implement one.

More than one valid value can be returned for a single `apiKeyId` — e.g. one per issued token — so a
single leaked or retired key can be dropped without invalidating the others. The request is
authenticated if `presentedApiKey` matches any of the returned values.

Each returned `SplibApiKeyExpectedValue` carries its own `SplibApiKeyComparisonMode` (plain text or
bcrypt) rather than a single application-wide setting, so one call can mix both — e.g. while
migrating stored keys from plain text to bcrypt one row at a time. See
[Comparison Modes](#comparison-modes) below.

If no `SplibApiKeyExpectedValueProvider` bean is registered at all, every request to `/api/key/**` is
rejected — there is no default "no key required" behavior for this prefix, unlike `/api/public/**`.

## Comparison Modes

### `PLAIN`

The configured value is the key itself, compared directly (in constant time) against the
presented value.

### `BCRYPT`

The configured value is a bcrypt hash of the key, rather than the key itself, so the raw key is
never at rest anywhere the application can read it back. The presented value is checked against
it via Spring Security's `BCryptPasswordEncoder.matches`.

Generate the hash to store with `BCryptPasswordEncoder`, the same way you would for a stored user
password:

```java
new BCryptPasswordEncoder().encode(rawApiKey)
```

To generate one from the command line, without going through the app, `htpasswd` works:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

Because bcrypt is intentionally slow, comparing against more than one value costs proportionally
more.

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
`X-Api-Key-Id` was sent) with the `ROLE_API_KEY` authority. Whether `/api/key/**` is reachable at
all is already decided by this point — the filter's own rejection — so the authorization rule
behind it is just `permitAll`, and `ROLE_API_KEY` doesn't restrict access on its own. Use it in a
controller, e.g. `@PreAuthorize("hasAuthority('ROLE_API_KEY')")`, to tell whether a given call came
in via API-key authentication.

## Giving different keys different authorities

As long as you use `SplibApiKeyExpectedValue(value, mode)` (two arguments), every key that
authenticates gets the same `ROLE_API_KEY` and nothing more. To grant different authorities per
key, use the three-argument constructor and pass `extraAuthorities`. Whichever entry matched has
its `extraAuthorities` granted in addition to `ROLE_API_KEY`.

```java
new SplibApiKeyExpectedValue(key, mode, List.of("ROLE_ADMIN_API_KEY"))
```
