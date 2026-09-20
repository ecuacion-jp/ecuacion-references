Some exception handlers need to send the user back to "the page they came from" after an error —
e.g. [Exception Handling](page?id=web/exception-handling&lang=en)'s handling of a
`ViolationException` raised outside a `SplibGeneralController` (no forms, so no page of its own to
re-render). The only record the app has of that page at that point is the client-supplied
`Referer` header, so it's used as the redirect target — but a client-supplied value handed
straight to a `Location` header is a classic open-redirect hole (CWE-601), so it's never trusted
as-is. `RefererRedirectUtil.toSameOriginRedirectTarget` extracts a same-origin path from it first.

## What it does

Given a `Referer` value, it keeps only the path (and query string, if any) — never the scheme or
host — since those are exactly what an attacker controls to point the redirect off-site.

```java
String target = RefererRedirectUtil.toSameOriginRedirectTarget(referer);
return new ModelAndView("redirect:" + target);
```

## Why dropping the scheme/host alone isn't enough

`java.net.URI` is expected to strip the scheme and host from an ordinary
`Referer: https://evil.example/x`, leaving just `/x` — and that much also holds for a "clean"
`//host/path` value on its own, since `URI` parses the part after `//` as the authority. The gap is
a `Referer` an attacker can still fully control despite that: one whose *path component itself*
starts with an extra `//`, e.g. `https://attacker.example//evil.example/x` (or a scheme-less
`////evil.example/x`) — a URL an attacker can host as-is and link a victim through, so the browser
sends it verbatim as `Referer`. `URI` treats `attacker.example` as the authority here and returns
the path unchanged as `//evil.example/x`, because a path is allowed to contain slashes and `URI`
doesn't re-interpret it.

Concatenated after `"redirect:"`, that value reaches Spring's `RedirectView` unchanged, and the
resulting `Location: //evil.example/x` response header is interpreted by every browser as a
*protocol-relative* absolute URL — same scheme, different host — sending the user to
`evil.example` instead of back into the app.

## The check

The extracted target must start with exactly one `/`. If it starts with `//` (or, degenerately,
doesn't start with `/` at all), the input is discarded and `/` is returned instead of trusting it.
