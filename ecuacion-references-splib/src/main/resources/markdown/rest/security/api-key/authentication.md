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

- The source IP is currently locked out (see [Rate Limiting](#rate-limiting-brute-force-protection)
  below).
- The `X-Api-Key` header is missing or empty.
- No `SplibApiKeyExpectedValueProvider` bean is registered.
- The provider returns `null` or an empty collection (e.g. unknown `apiKeyId`).
- The presented key does not match the expected value.

Details are logged server-side only; the presented key value itself is never logged. The comparison
uses `MessageDigest.isEqual` (constant-time) to avoid a timing attack.

## Rate Limiting (Brute-Force Protection)

Each key mismatch counts against a per-source-IP failure count, entirely in-memory (JVM heap, no
database). Once a source IP accumulates `max-failures` mismatches within `window-seconds`, it's
locked out for `lockout-seconds` — rejected with the same generic `401` above, without even
attempting a key comparison. This also bounds the CPU cost of `BCRYPT` mode: an attacker forcing a
bcrypt comparison against every registered key on every guess gets cut off after `max-failures`
guesses rather than being able to repeat indefinitely. A successful match clears the count for that
IP.

| Property | Type | Description |
| --- | --- | --- |
| `jp.ecuacion.splib.rest.api-key.rate-limit.max-failures` | int | Mismatches allowed within the window before lockout. Default: `10`. |
| `jp.ecuacion.splib.rest.api-key.rate-limit.window-seconds` | long | The sliding window the count above applies to. Default: `60`. |
| `jp.ecuacion.splib.rest.api-key.rate-limit.lockout-seconds` | long | How long a source IP stays locked out once triggered. Default: `300`. |

Being in-memory, the count resets on restart and isn't shared across instances behind a load
balancer — each instance tracks its own source IPs independently. For this module's usual
single-instance deployment that's an acceptable trade-off for not requiring a database.

**Behind a reverse proxy:** the source IP is `HttpServletRequest.getRemoteAddr()` — the immediate
TCP peer. Behind a reverse proxy (nginx, an ALB, etc.) without further configuration, that's the
proxy's own address for every request, which would bucket all traffic behind it under one IP. Don't
address this by trusting `X-Forwarded-For` here — it's a header any direct caller can also set, so
trusting it blindly would let an attacker spoof a fresh IP on every request and bypass the lockout
entirely. Instead, if the proxy is trusted to set (and overwrite any client-supplied)
`X-Forwarded-For` correctly, enable Spring Boot's own
[`server.forward-headers-strategy=native`](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.embedded-container.customizing.programmatic)
(or `framework`) — `getRemoteAddr()` then already reflects the real client IP by the time it
reaches this filter, and this filter itself stays unaware the proxy exists.

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
