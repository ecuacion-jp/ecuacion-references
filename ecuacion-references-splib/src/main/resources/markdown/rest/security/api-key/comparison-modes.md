`SplibApiKeyComparisonMode` selects how one value returned by your
[`SplibApiKeyExpectedValueProvider`](page?id=rest/security/api-key/overview&lang=en)
is compared against the client-presented `X-Api-Key` header. It is carried per value, via
`SplibApiKeyExpectedValue`:

```java
new SplibApiKeyExpectedValue(storedValue, SplibApiKeyComparisonMode.BCRYPT)
```

There is no application-wide switch — a single call to `getExpectedValues` can freely return a mix
of `PLAIN` and `BCRYPT` values. This is what makes migrating stored keys from plain text to bcrypt
practical: convert rows one at a time, with both kinds accepted throughout the migration, rather
than flipping every key over in one step.

This mode selection is specific to `/api/key/**`. [Built-in Key Endpoints](page?id=rest/security/builtin-api-key/overview&lang=en)
(`/api/ecuacion-splib/key/**`) does not use `SplibApiKeyComparisonMode` at all — its credential is
configured directly via `jp.ecuacion.splib.rest.builtin-api-key.password-plain` or
`...password-bcrypt` instead, with no separate mode property.

## `PLAIN`

`SplibApiKeyExpectedValue.value()` is the key itself, compared directly (in constant time) against
the presented value.

## `BCRYPT`

`SplibApiKeyExpectedValue.value()` is a bcrypt hash of the key, rather than the key itself, so the
raw key is never at rest anywhere the application can read it back. Each presented header value is
checked against every `BCRYPT` value via Spring Security's `BCryptPasswordEncoder.matches`, never
short-circuiting on the first match.

Generate the hash to store with `BCryptPasswordEncoder`, the same way you would for a stored user
password:

```java
new BCryptPasswordEncoder().encode(rawApiKey)
```

To generate one from the command line, without going through the app, `htpasswd` works:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

Because bcrypt is intentionally slow, a request is checked once per value `getExpectedValues`
returns — keep that collection small (e.g. narrow it down using the `X-Api-Key-Id` header) rather
than returning every issued key on every request.
